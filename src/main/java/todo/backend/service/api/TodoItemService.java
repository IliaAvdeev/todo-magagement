package todo.backend.service.api;

import todo.backend.model.entity.TodoItemEntity;

import java.util.List;

public interface TodoItemService extends CRUDService<TodoItemEntity> {
    List<TodoItemEntity> getAll(boolean filteredOnlyNotDoneItems);
}