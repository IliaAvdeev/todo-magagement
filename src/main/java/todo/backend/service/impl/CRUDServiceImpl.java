package todo.backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import todo.backend.mapper.EntityMerger;
import todo.backend.model.entity.HasId;
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
        return repository.save(entity);
    }

    @Override
    public E patch(E entity) {
        E savedEntity = repository.findById(entity.getId()).orElseThrow();
        entityMerger.merge(entity, savedEntity);
        return repository.save(savedEntity);
    }

    @Override
    public List<E> getAll() {
        return repository.findAll();
    }

    @Override
    public E getById(UUID entityId) {
        return repository.findById(entityId).orElseThrow(() -> new RuntimeException("Entity doesn't exists"));
    }
}