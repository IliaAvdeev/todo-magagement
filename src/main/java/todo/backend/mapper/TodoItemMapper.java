package todo.backend.mapper;

import org.mapstruct.Mapper;
import todo.backend.model.dto.TodoItemDto;
import todo.backend.model.entity.TodoItemEntity;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface TodoItemMapper extends DtoMapper<TodoItemDto, TodoItemEntity> {}