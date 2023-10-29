package todo.backend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import todo.backend.model.validation.annotation.ValidTodoStatus;

import java.util.Set;

import static todo.backend.model.entity.TodoItemStatus.DONE;
import static todo.backend.model.entity.TodoItemStatus.NOT_DONE;

public class ValidTodoStatusValidator implements ConstraintValidator<ValidTodoStatus, String> {
    private final Set<String> AVAILABLE_TODO_ITEM_STATUSES = Set.of(DONE.getValue(), NOT_DONE.getValue());

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return AVAILABLE_TODO_ITEM_STATUSES.contains(value);
    }
}