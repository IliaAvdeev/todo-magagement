package todo.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import todo.backend.model.entity.TodoItemEntity;

import java.util.UUID;

@Repository
public interface TodoItemRepository extends JpaRepository<TodoItemEntity, UUID> {}