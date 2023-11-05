package todo.backend.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import todo.backend.model.dto.TodoItemDto;
import todo.backend.model.entity.TodoItemEntity;
import todo.backend.model.exception.TodoManagementException;
import todo.backend.repository.TodoItemRepository;
import todo.backend.validation.api.CRUDValidator;

import java.util.UUID;

import static todo.backend.model.entity.TodoItemStatus.DONE;

@Component
@RequiredArgsConstructor
public class TodoItemValidatorImpl implements CRUDValidator<TodoItemDto> {
    private final TodoItemRepository todoItemRepository;

    @Override
    public void validatePatch(TodoItemDto todoItemDto) {
        UUID id = todoItemDto.getId();
        String status = todoItemDto.getStatus();
        if (status == null) {
            TodoItemEntity savedEntity = todoItemRepository.findById(id).orElse(null);
            if (savedEntity != null && DONE.is(savedEntity.getStatus())) {
                throw new TodoManagementException("It is prohibited to change the data of a todo element (except 'status' property), " +
                        "which is in the 'Done' status");
            }
        }
    }
}