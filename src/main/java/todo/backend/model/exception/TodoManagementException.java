package todo.backend.model.exception;

public class TodoManagementException extends RuntimeException {
    public TodoManagementException(String message) {
        super(message);
    }
}