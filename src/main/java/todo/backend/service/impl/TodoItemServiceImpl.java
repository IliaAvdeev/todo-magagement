package todo.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import todo.backend.model.entity.TodoItemEntity;
import todo.backend.model.entity.TodoItemStatus;
import todo.backend.repository.TodoItemRepository;
import todo.backend.service.api.TodoItemService;

import java.time.LocalDateTime;
import java.util.List;

import static todo.backend.model.entity.TodoItemStatus.NOT_DONE;

@Service
@RequiredArgsConstructor
public class TodoItemServiceImpl extends CRUDServiceImpl<TodoItemEntity> implements TodoItemService {
    private final TodoItemRepository todoItemRepository;

    @Override
    public List<TodoItemEntity> getAll(boolean filteredOnlyNotDoneItems) {
        return filteredOnlyNotDoneItems ? todoItemRepository.findAllByStatus(NOT_DONE.getValue()) : getAll();
    }

    @Override
    protected void preCreate(TodoItemEntity entity) {
        entity.setStatus(NOT_DONE.getValue());
        super.preCreate(entity);
    }

    @Override
    protected void prePatch(TodoItemEntity entity) {
        if (TodoItemStatus.DONE.is(entity.getStatus())) {
            entity.setMarkedDoneDatetime(LocalDateTime.now());
        }
        if (NOT_DONE.is(entity.getStatus())) {
            entity.setMarkedDoneDatetime(null);
        }
        super.prePatch(entity);
    }
}