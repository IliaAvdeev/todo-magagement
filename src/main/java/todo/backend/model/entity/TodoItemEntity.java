package todo.backend.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Entity
public class TodoItemEntity {
    @Id
    @GeneratedValue
    private UUID id;
    private TodoStatus status;
    private String description;
    private OffsetDateTime createdTime;
    private OffsetDateTime dueDatetime;
    private OffsetDateTime markedDoneTime;
}