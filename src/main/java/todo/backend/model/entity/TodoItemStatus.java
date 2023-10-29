package todo.backend.model.entity;

import lombok.Getter;

@Getter
public enum TodoItemStatus {
    DONE("Done"),
    NOT_DONE("Not done"),
    PAST_DUE("Past due");

    private final String value;

    TodoItemStatus(String value) {
        this.value = value;
    }

    public boolean is(String status) {
        return this.value.equals(status);
    }
}