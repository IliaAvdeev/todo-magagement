package todo.backend.mapper;

import java.util.List;

public interface DtoMapper<D, E> {
    E toEntity(D dto);

    D toDto(E entity);

    List<D> toDto(List<E> entities);
}