package todo.backend.validation.api;

public interface CRUDValidator<E> {
    void validatePatch(E entity);
}