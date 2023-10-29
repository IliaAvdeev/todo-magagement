package todo.backend.errorhandler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import todo.backend.model.exception.ExceptionRecords;
import todo.backend.model.exception.TodoManagementException;

@ControllerAdvice
public class TodoManagementExceptionHandler {
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException constraintViolationException) {
        ExceptionRecords.ErrorContainer errorContainer = constraintViolationException.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .map(message -> new ExceptionRecords.ErrorEntry("TODO-0001", message))
                .findFirst()
                .map(ExceptionRecords.ErrorContainer::new)
                .orElse(null);

        return ResponseEntity
                .unprocessableEntity()
                .body(errorContainer);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleConstraintViolationException(MethodArgumentNotValidException methodArgumentNotValidException) {
        BindingResult bindingResult = methodArgumentNotValidException.getBindingResult();
        FieldError fieldError = bindingResult.getFieldError();
        String message = fieldError.getDefaultMessage();
        ExceptionRecords.ErrorEntry errorEntry = new ExceptionRecords.ErrorEntry("TODO-0001", message);

        return ResponseEntity
                .unprocessableEntity()
                .body(new ExceptionRecords.ErrorContainer(errorEntry));
    }

    @ExceptionHandler(TodoManagementException.class)
    public ResponseEntity<?> handleTodoManagementException(TodoManagementException todoManagementException) {
        ExceptionRecords.ErrorEntry errorEntry
                = new ExceptionRecords.ErrorEntry("TODO-0001", todoManagementException.getMessage());

        return ResponseEntity
                .unprocessableEntity()
                .body(new ExceptionRecords.ErrorContainer(errorEntry));
    }
}