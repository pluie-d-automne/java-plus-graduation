package ewm.core.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFullDto {
        private Long id;
        private String annotation;
        private CategoryDto category;
        private Long confirmedRequests;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdOn;

        private String description;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime eventDate;

        private UserShortDto initiator;
        private LocationDto location;
        private Boolean paid;

        @PositiveOrZero
        private Integer participantLimit;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime publishedOn;

        private Boolean requestModeration;
        private EventState state;
        private String title;
        private Double rating;
}
