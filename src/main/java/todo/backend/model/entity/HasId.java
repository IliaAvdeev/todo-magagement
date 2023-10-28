package todo.backend.model.entity;

import java.util.UUID;

public interface HasId {
    UUID getId();

    void setId(UUID id);
}