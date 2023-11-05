package todo.backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import todo.backend.merger.EntityMerger;
import todo.backend.model.entity.HasId;
import todo.backend.model.exception.TodoManagementException;
import todo.backend.service.api.CRUDService;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public abstract class CRUDServiceImpl<E extends HasId> implements CRUDService<E> {
    @Autowired
    private EntityMerger<E> entityMerger;
    @Autowired
    private JpaRepository<E, UUID> repository;

    @Override
    public E create(E entity) {
        preCreate(entity);
        return repository.save(entity);
    }

    @Override
    public E patch(E entity) {
        UUID entityId = entity.getId();
        E savedEntity = repository.findById(entityId)
                .orElseThrow(() -> new TodoManagementException("Entity with id '" + entityId + "' doesn't exists"));
        entityMerger.merge(entity, savedEntity);
        prePatch(savedEntity);
        return repository.save(savedEntity);
    }

    @Override
    public List<E> getAll() {
        return repository.findAll();
    }

    @Override
    public E getById(UUID entityId) {
        return repository.findById(entityId)
                .orElseThrow(() -> new TodoManagementException("Entity with id '" + entityId + "' doesn't exists"));
    }

    protected void preCreate(E entity) {}

    protected void prePatch(E entity) {}
}