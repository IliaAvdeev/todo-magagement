package todo.backend.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;
import todo.backend.config.LocalDateTimeDeserializer;
import todo.backend.model.validation.annotation.ValidTodoStatus;

import java.time.LocalDateTime;
import java.util.UUID;

import static todo.backend.config.JacksonConfig.GLOBAL_DATETIME_FORMAT;
import static todo.backend.model.validation.ValidationGroups.Create;
import static todo.backend.model.validation.ValidationGroups.Patch;

@Data
public class TodoItemDto {
    private UUID id;

    @NotBlank(message = "The 'description' field must be filled", groups = Create.class)
    private String description;

    @Null(message = "The 'status' field must be null", groups = Create.class)
    @ValidTodoStatus(message = "Incorrect value for status: '${validatedValue}'. " +
            "The status can be only ether 'Done' or 'Not Done'", groups = Patch.class)
    private String status;

    @JsonFormat(pattern = GLOBAL_DATETIME_FORMAT)
    @Null(message = "The 'createdTime' field must be null")
    private LocalDateTime createdDatetime;

    @JsonFormat(pattern = GLOBAL_DATETIME_FORMAT)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @FutureOrPresent(message = "The 'dueDatetime' field can't be in past")
    @NotNull(message = "The 'dueDatetime' field must be filled", groups = Create.class)
    private LocalDateTime dueDatetime;

    @JsonFormat(pattern = GLOBAL_DATETIME_FORMAT)
    @Null(message = "The 'markedDoneTime' field must be null")
    private LocalDateTime markedDoneDatetime;
}