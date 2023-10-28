package todo.backend.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Entity
public class TodoItemEntity implements HasId {
    @Id
    @GeneratedValue
    private UUID id;
    private TodoStatus status;
    private String description;
    @CreationTimestamp
    private OffsetDateTime createdTime;
    private OffsetDateTime dueDatetime;
    private OffsetDateTime markedDoneTime;
}