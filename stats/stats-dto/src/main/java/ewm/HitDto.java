package ewm;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record HitDto(
        @NotBlank(message = "Идентификатор сервиса для которого записывается информация не может быть пустым")
        String app,

        @NotBlank(message = "URI для которого был осуществлен запрос не может быть пустым")
        String uri,

        @NotBlank(message = "IP-адрес пользователя, осуществившего запрос, не может быть пустым")
        String ip,

        @NotNull(message = "Дата не может быть пустой")
        LocalDateTime timestamp) {
}
