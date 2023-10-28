package todo.backend.model.dto;

import lombok.Data;
import todo.backend.model.entity.TodoStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class TodoItemDto {
    private UUID id;
    private TodoStatus status;
    private String description;
    private OffsetDateTime createdTime;
    private OffsetDateTime dueDatetime;
    private OffsetDateTime markedDoneTime;
}