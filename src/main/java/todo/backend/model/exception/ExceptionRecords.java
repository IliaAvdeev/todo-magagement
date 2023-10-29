package todo.backend.model.exception;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ExceptionRecords {
    public record ErrorContainer(ErrorEntry error) {}

    public record ErrorEntry(String code, String errorMessage) {}
}