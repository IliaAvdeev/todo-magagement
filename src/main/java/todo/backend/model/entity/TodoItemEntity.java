package todo.backend.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
public class TodoItemEntity implements HasId {
    @Id
    @GeneratedValue
    private UUID id;
    private String status;
    private String description;
    private LocalDateTime dueDatetime;
    @CreationTimestamp
    private LocalDateTime createdDatetime;
    private LocalDateTime markedDoneDatetime;
}