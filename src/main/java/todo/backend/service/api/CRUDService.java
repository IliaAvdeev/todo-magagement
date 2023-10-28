package todo.backend.service.api;

import todo.backend.model.entity.HasId;

import java.util.List;
import java.util.UUID;

public interface CRUDService<E extends HasId> {
    List<E> getAll();

    E patch(E entity);

    E create(E entity);

    E getById(UUID entityId);
}