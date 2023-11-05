package todo.backend.merger;

import org.mapstruct.Mapper;
import todo.backend.model.entity.TodoItemEntity;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface TodoItemMerger extends EntityMerger<TodoItemEntity> {}