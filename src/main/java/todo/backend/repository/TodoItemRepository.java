package todo.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import todo.backend.model.entity.TodoItemEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TodoItemRepository extends JpaRepository<TodoItemEntity, UUID> {
    List<TodoItemEntity> findAllByStatus(String status);

    @Modifying
    @Query("update TodoItemEntity todo set todo.status = 'Past due' where todo.dueDatetime < :date and todo.status = 'Not done'")
    int markTodoItemsThatHaveExpired(@Param("date") LocalDateTime date);
}