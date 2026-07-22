package ewm.core.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ewm.core.category.model.Category;
import ewm.core.dto.UserShortDto;
import ewm.core.event.model.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventShortDto {
        private Long id;
        private String annotation;
        private Category category;
        private Long confirmedRequests;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime eventDate;

        private UserShortDto initiator;
        private Boolean paid;
        private String title;
        private Long views;
        private Location location;
}