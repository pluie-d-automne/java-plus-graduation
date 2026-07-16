package ewm;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record ParamDto(
        @NotNull(message = "Дата и время начала диапазона за который нужно выгрузить статистику обязательны")
        LocalDateTime start,

        @NotNull(message = "Дата и время конца диапазона за который нужно выгрузить статистику обязательны")
        LocalDateTime end,

        List<String> uris,

        Boolean unique) {
}
