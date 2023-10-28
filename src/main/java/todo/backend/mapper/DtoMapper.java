package todo.backend.mapper;

public interface DtoMapper<D, E> {
    E toEntity(D dto);

    D toDto(E entity);
}