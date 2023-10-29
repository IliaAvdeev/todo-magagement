package todo.backend.service.impl;

import org.springframework.stereotype.Service;
import todo.backend.model.entity.TodoItemEntity;
import todo.backend.model.entity.TodoItemStatus;
import todo.backend.service.api.TodoItemService;

import java.time.LocalDateTime;

@Service
public class TodoItemServiceImpl extends CRUDServiceImpl<TodoItemEntity> implements TodoItemService {
    @Override
    protected void preCreate(TodoItemEntity entity) {
        entity.setStatus(TodoItemStatus.NOT_DONE.getValue());
        super.preCreate(entity);
    }

    @Override
    protected void prePatch(TodoItemEntity entity) {
        if (TodoItemStatus.DONE.is(entity.getStatus())) {
            entity.setMarkedDoneTime(LocalDateTime.now());
        }
        if (TodoItemStatus.NOT_DONE.is(entity.getStatus())) {
            entity.setMarkedDoneTime(null);
        }
        super.prePatch(entity);
    }
}