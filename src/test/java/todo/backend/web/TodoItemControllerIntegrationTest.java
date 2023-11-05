package todo.backend.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import todo.backend.model.dto.TodoItemDto;
import todo.backend.model.entity.TodoItemEntity;
import todo.backend.repository.TodoItemRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static todo.backend.config.JacksonConfig.GLOBAL_DATETIME_FORMAT;
import static todo.backend.model.entity.TodoItemStatus.DONE;
import static todo.backend.model.entity.TodoItemStatus.NOT_DONE;
import static todo.backend.model.entity.TodoItemStatus.PAST_DUE;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class TodoItemControllerIntegrationTest {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(GLOBAL_DATETIME_FORMAT);

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TodoItemRepository todoItemRepository;

    @AfterEach
    public void tearDown() {
        todoItemRepository.deleteAll();
    }

    @Test
    public void testCreate() throws Exception {
        TodoItemDto expected = createStubTodoItemDto();

        String createdTodoItem = mockMvc.perform(post("/todo-management/todo")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(expected)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.dueDatetime").isNotEmpty())
                .andExpect(jsonPath("$.status").value("Not done"))
                .andExpect(jsonPath("$.description").value("Create test #1"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        UUID id = extractId(createdTodoItem);

        TodoItemEntity actual = todoItemRepository.findById(id).orElse(null);

        assertTodoItemEntity(actual, expected);
    }

    @Test
    public void testGetList() throws Exception {
        createTodoEntities(10);
        // Pattern compile = Pattern.compile("^([1-9]|([012][0-9])|(3[01]))/(0?[1-9]|1[012])/\\d\\d\\d\\d\\s([0-1]?[0-9]|2?[0-3]):([0-5]\\d)$");

        mockMvc.perform(get("/todo-management/todo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(10)))
                .andExpect(jsonPath("$.[*].status", hasItem("Not done")))
                //  .andExpect(jsonPath("$.[*].dueDatetime",everyItem(matchesRegex(compile))))
                .andExpect(jsonPath("$.[*].id", everyItem(is(not(empty())))))
                .andExpect(jsonPath("$.[*].markedDoneTime", everyItem(is(empty()))))
                .andExpect(jsonPath("$.[*].dueDatetime", everyItem(is(not(empty())))))
                .andExpect(jsonPath("$.[*].createdTime", everyItem(is(not(empty())))))
                .andExpect(jsonPath("$.[*].description", everyItem(startsWith("Create test #"))));

        List<TodoItemEntity> allTodoItemEntities = todoItemRepository.findAll();
        assertEquals(10, allTodoItemEntities.size());
    }

    @Test
    public void testPatch() throws Exception {
        String createdTodoItem = createTodoEntity();

        String patchedTodoItem = mockMvc.perform(patch("/todo-management/todo/{id}", extractId(createdTodoItem))
                        .contentType("application/json")
                        .content("""
                                {
                                "description": "New description"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.status").value("Not done"))
                .andExpect(jsonPath("$.description").value("New description"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        UUID id = extractId(patchedTodoItem);

        TodoItemEntity actual = todoItemRepository.findById(id).orElse(null);

        assertNotNull(actual);
        assertEquals(NOT_DONE.getValue(), actual.getStatus());
        assertEquals("New description", actual.getDescription());
        assertNull(actual.getMarkedDoneTime());
    }

    @Test
    public void testGetListWithFilteredOnlyNotDoneItemsOptions() throws Exception {
        createTodoEntities(6);
        createFutureTodoEntities(3);

        Thread.sleep(61_000);

        String filteredTodoItems = mockMvc.perform(get("/todo-management/todo")
                        .param("filteredOnlyNotDoneItems", "true"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<TodoItemDto> filteredItems = List.of(objectMapper.readValue(filteredTodoItems, TodoItemDto[].class));
        assertEquals(6, filteredItems.size());
        filteredItems.forEach(todoItemEntity -> assertEquals(NOT_DONE.getValue(), todoItemEntity.getStatus()));
    }

    @Test
    public void testScheduleJobForPastDueItems() throws Exception {
        createTodoEntities(7);
        createFutureTodoEntities(4);

        Thread.sleep(61_000);

        mockMvc.perform(get("/todo-management/todo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(11)))
                .andExpect(jsonPath("$.[*].status", everyItem(anyOf(is("Not done"), is("Past due")))))
                //  .andExpect(jsonPath("$.[*].dueDatetime",everyItem(matchesRegex(compile))))
                .andExpect(jsonPath("$.[*].id", everyItem(is(not(empty())))))
                .andExpect(jsonPath("$.[*].markedDoneTime", everyItem(is(empty()))))
                .andExpect(jsonPath("$.[*].dueDatetime", everyItem(is(not(empty())))))
                .andExpect(jsonPath("$.[*].createdTime", everyItem(is(not(empty())))))
                .andExpect(jsonPath("$.[*].description", everyItem(startsWith("Create test #"))));

        List<TodoItemEntity> entities = todoItemRepository.findAll();
        assertEquals(11, entities.size());

        List<TodoItemEntity> pastDueTodoItems = todoItemRepository.findAllByStatus(PAST_DUE.getValue());
        List<TodoItemEntity> notDoneTodoItems = todoItemRepository.findAllByStatus(NOT_DONE.getValue());

        assertEquals(4, pastDueTodoItems.size());
        assertEquals(7, notDoneTodoItems.size());

        pastDueTodoItems.forEach(todoItemEntity -> assertEquals(PAST_DUE.getValue(), todoItemEntity.getStatus()));
        notDoneTodoItems.forEach(todoItemEntity -> assertEquals(NOT_DONE.getValue(), todoItemEntity.getStatus()));
    }

    @Test
    public void testCreateWhenDueDatetimeIsNull() throws Exception {
        TodoItemDto todoItemDto = createStubTodoItemDto();
        todoItemDto.setDueDatetime(null);
        mockMvc.perform(post("/todo-management/todo")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(todoItemDto)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error.errorMessage").value("The 'dueDatetime' field must be filled"));
    }

    @Test
    public void testCreateWhenDescriptionIsNull() throws Exception {
        TodoItemDto todoItemDto = createStubTodoItemDto();
        todoItemDto.setDescription(null);
        mockMvc.perform(post("/todo-management/todo")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(todoItemDto)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error.errorMessage").value("The 'description' field must be filled"));
    }

    @Test
    public void testCreateWhenDescriptionIsEmpty() throws Exception {
        TodoItemDto todoItemDto = createStubTodoItemDto();
        todoItemDto.setDescription("");
        mockMvc.perform(post("/todo-management/todo")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(todoItemDto)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error.errorMessage").value("The 'description' field must be filled"));
    }

    @Test
    public void testCreateWhenCreatedTimeIsNotNull() throws Exception {
        TodoItemDto todoItemDto = createStubTodoItemDto();
        todoItemDto.setCreatedTime(LocalDateTime.now());
        mockMvc.perform(post("/todo-management/todo")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(todoItemDto)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error.errorMessage").value("The 'createdTime' field must be null"));
    }

    @Test
    public void testCreateWhenMarkedDoneTimeIsNotNull() throws Exception {
        TodoItemDto todoItemDto = createStubTodoItemDto();
        todoItemDto.setMarkedDoneTime(LocalDateTime.now());
        mockMvc.perform(post("/todo-management/todo")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(todoItemDto)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error.errorMessage").value("The 'markedDoneTime' field must be null"));
    }

    @Test
    public void testCreateWhenStatusIsNotNull() throws Exception {
        TodoItemDto todoItemDto = createStubTodoItemDto();
        todoItemDto.setStatus(DONE.getValue());
        mockMvc.perform(post("/todo-management/todo")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(todoItemDto)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error.errorMessage").value("The 'status' field must be null"));
    }

    @Test
    public void testCreateWhenDueDateTimeIsInPast() throws Exception {
        TodoItemDto todoItemDto = createStubTodoItemDto();
        todoItemDto.setDueDatetime(LocalDateTime.now().minusDays(1));
        mockMvc.perform(post("/todo-management/todo")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(todoItemDto)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error.errorMessage").value("The 'dueDatetime' field can't be in past"));
    }

    @Test
    public void testPatchWhenStatusIsDone() throws Exception {
        String createdTodoEntity = createTodoEntity();

        String patchedTodoEntity = mockMvc.perform(patch("/todo-management/todo/{id}", extractId(createdTodoEntity))
                        .contentType("application/json")
                        .content("""
                                {
                                "status": "Done"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.markedDoneTime").isNotEmpty())
                .andExpect(jsonPath("$.status").value("Done"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        UUID id = extractId(patchedTodoEntity);

        TodoItemEntity actual = todoItemRepository.findById(id).orElse(null);

        assertNotNull(actual);
        assertNotNull(actual.getMarkedDoneTime());
        assertEquals(DONE.getValue(), actual.getStatus());
    }

    @Test
    public void testPatchWhenStatusIsIncorrect() throws Exception {
        String createdTodoItem = createTodoEntity();

        mockMvc.perform(patch("/todo-management/todo/{id}", extractId(createdTodoItem))
                        .contentType("application/json")
                        .content("""
                                {
                                "status": "Wrong status"
                                }
                                """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error.errorMessage")
                        .value("Incorrect value for status: 'Wrong status'. The status can be only ether 'Done' or 'Not Done'"));
    }

    @Test
    public void testCreateWhenDateFormatIsIncorrect() throws Exception {
        String body = """
                {
                    "description": "Make a new description for a current task",
                    "dueDatetime": "03-12-2023"
                }
                """;
        mockMvc.perform(post("/todo-management/todo")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error.errorMessage")
                        .value("Field 'dueDatetime' does not match date format 'dd/MM/yyyy HH:mm'"));
    }

    @Test
    public void testGetWithNonExistentId() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(get("/todo-management/todo/{id}", id))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error.errorMessage")
                        .value("Entity with id '" + id + "' doesn't exists"));
    }

    @Test
    public void testPatchDoneItem() throws Exception {
        String createdTodoItem = createTodoEntity();

        UUID id = extractId(createdTodoItem);

        mockMvc.perform(patch("/todo-management/todo/{id}", id)
                        .contentType("application/json")
                        .content("""
                                {
                                "status": "Done"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.markedDoneTime").isNotEmpty())
                .andExpect(jsonPath("$.status").value("Done"));

        mockMvc.perform(patch("/todo-management/todo/{id}", id)
                        .contentType("application/json")
                        .content("""
                                {
                                "description": "New description"
                                }
                                """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error.errorMessage")
                        .value("It is prohibited to change the data of a todo element (except 'status' property), " +
                                "which is in the 'Done' status")
                );
    }

    private void assertTodoItemEntity(TodoItemEntity actual, TodoItemDto expected) {
        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertNotNull(actual.getCreatedTime());
        assertEquals(NOT_DONE.getValue(), actual.getStatus());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getDueDatetime().format(DATE_TIME_FORMATTER), actual.getDueDatetime().format(DATE_TIME_FORMATTER));
    }

    private TodoItemDto createStubTodoItemDto() {
        return createStubTodoItemDto(1);
    }

    private TodoItemDto createStubTodoItemDto(int i) {
        TodoItemDto todoItemDto = new TodoItemDto();
        todoItemDto.setDescription("Create test #" + i);
        todoItemDto.setDueDatetime(LocalDateTime.now().plusDays(1));
        return todoItemDto;
    }

    private UUID extractId(String todoItem) throws JsonProcessingException {
        TodoItemDto value = objectMapper.readValue(todoItem, TodoItemDto.class);
        return value.getId();
    }

    private String createTodoEntity() throws Exception {
        return createTodoEntity(createStubTodoItemDto(1));
    }

    private void createTodoEntity(int i) throws Exception {
        createTodoEntity(createStubTodoItemDto(i));
    }

    private String createTodoEntity(TodoItemDto todoItemDto) throws Exception {
        return mockMvc.perform(post("/todo-management/todo")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(todoItemDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    private void createTodoEntities(int numberOfEntity) throws Exception {
        for (int i = 1; i < numberOfEntity + 1; i++) {
            createTodoEntity(i);
        }
    }

    private void createFutureTodoEntities(int numberOfEntity) throws Exception {
        for (int i = 1; i < numberOfEntity + 1; i++) {
            TodoItemDto todoItemDto = createStubTodoItemDto(i);
            todoItemDto.setDueDatetime(LocalDateTime.now().plusMinutes(1));
            createTodoEntity(todoItemDto);
        }
    }
}