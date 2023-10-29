package todo.backend.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import todo.backend.model.exception.TodoManagementException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static todo.backend.config.JacksonConfig.GLOBAL_DATETIME_FORMAT;

public class LocalDateTimeDeserializer extends StdDeserializer<LocalDateTime> {
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(GLOBAL_DATETIME_FORMAT);

    public LocalDateTimeDeserializer() {
        this(null);
    }

    protected LocalDateTimeDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String date = jsonParser.getText();
        try {
            return LocalDateTime.parse(date, dateTimeFormatter);
        } catch (DateTimeParseException dateTimeParseException) {
            String property = jsonParser.currentName();
            throw new TodoManagementException("Field '" + property + "' does not match date format '" + GLOBAL_DATETIME_FORMAT + "'");
        }
    }
}