package todo.backend.errorhandler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import todo.backend.model.exception.ExceptionRecords;
import todo.backend.model.exception.TodoManagementException;

@ControllerAdvice
public class TodoManagementExceptionHandler {

    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(ConstraintViolationException.class)
    public ExceptionRecords.ErrorContainer handleConstraintViolationException(ConstraintViolationException constraintViolationException) {
        return constraintViolationException.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .map(message -> new ExceptionRecords.ErrorEntry("TODO-0001", message))
                .findFirst()
                .map(ExceptionRecords.ErrorContainer::new)
                .orElse(null);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ExceptionRecords.ErrorContainer handleConstraintViolationException(MethodArgumentNotValidException methodArgumentNotValidException) {
        BindingResult bindingResult = methodArgumentNotValidException.getBindingResult();
        FieldError fieldError = bindingResult.getFieldError();
        String message = fieldError == null ? "" : fieldError.getDefaultMessage();
        ExceptionRecords.ErrorEntry errorEntry = new ExceptionRecords.ErrorEntry("TODO-0002", message);

        return new ExceptionRecords.ErrorContainer(errorEntry);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(TodoManagementException.class)
    public ExceptionRecords.ErrorContainer handleTodoManagementException(TodoManagementException todoManagementException) {
        ExceptionRecords.ErrorEntry errorEntry
                = new ExceptionRecords.ErrorEntry("TODO-0003", todoManagementException.getMessage());

        return new ExceptionRecords.ErrorContainer(errorEntry);
    }
}