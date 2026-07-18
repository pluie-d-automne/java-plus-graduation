package ewm.core.mapper;

import ewm.core.dto.ParticipationRequestDto;
import ewm.core.model.ParticipationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ParticipationRequestMapper {
    @Mapping(target = "requester", source = "requesterId")
    @Mapping(target = "event", source = "eventId")
    ParticipationRequestDto mapToRequestDto(ParticipationRequest request);
}
