package todo.backend.service.impl;

import org.springframework.stereotype.Service;
import todo.backend.model.entity.TodoItemEntity;
import todo.backend.service.api.TodoItemService;

@Service
public class TodoItemServiceImpl extends CRUDServiceImpl<TodoItemEntity> implements TodoItemService {}