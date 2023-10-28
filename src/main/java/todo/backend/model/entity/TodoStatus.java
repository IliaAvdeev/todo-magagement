package todo.backend.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TodoStatus {
    @JsonProperty("Done")
    DONE,
    @JsonProperty("Not_done")
    NOT_DONE,
    @JsonProperty("Past_due")
    PAST_DUE;
}