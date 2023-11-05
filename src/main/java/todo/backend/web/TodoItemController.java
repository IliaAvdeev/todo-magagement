package todo.backend.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import todo.backend.aspect.Log;
import todo.backend.mapper.TodoItemMapper;
import todo.backend.model.dto.TodoItemDto;
import todo.backend.service.api.TodoItemService;
import todo.backend.validation.api.CRUDValidator;

import java.util.List;
import java.util.UUID;

import static todo.backend.model.validation.ValidationGroups.Create;
import static todo.backend.model.validation.ValidationGroups.Patch;

@Log
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/todo-management/todo")
public class TodoItemController {
    private final TodoItemMapper todoItemMapper;
    private final TodoItemService todoItemService;
    private final CRUDValidator<TodoItemDto> todoItemEntityValidator;

    @PostMapping
    @Validated(Create.class)
    @ResponseStatus(HttpStatus.CREATED)
    public TodoItemDto create(@Valid @RequestBody TodoItemDto todoItem) {
        return todoItemMapper.toDto(todoItemService.create(todoItemMapper.toEntity(todoItem)));
    }

    @Validated(Patch.class)
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TodoItemDto patch(@PathVariable UUID id, @Valid @RequestBody TodoItemDto todoItem) {
        todoItem.setId(id);
        todoItemEntityValidator.validatePatch(todoItem);
        return todoItemMapper.toDto(todoItemService.patch(todoItemMapper.toEntity(todoItem)));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TodoItemDto retrieve(@PathVariable UUID id) {
        return todoItemMapper.toDto(todoItemService.getById(id));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TodoItemDto> getList(@RequestParam(value = "filteredOnlyNotDoneItems", required = false) boolean filteredOnlyNotDoneItems) {
        return todoItemMapper.toDto(todoItemService.getAll(filteredOnlyNotDoneItems));
    }
}