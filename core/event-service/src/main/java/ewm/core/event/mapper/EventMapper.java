package ewm.core.event.mapper;

import ewm.core.category.model.Category;
import ewm.core.mapper.UserMapper;
import ewm.core.dto.EventFullDto;
import ewm.core.event.dto.EventShortDto;
import ewm.core.event.dto.NewEventDto;
import ewm.core.event.dto.UpdateEventUserRequest;
import ewm.core.event.model.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        uses = {UserMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EventMapper {

    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "views", ignore = true)
    EventShortDto toShortDto(Event event);

    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "views", ignore = true)
    EventFullDto toFullDto(Event event);

    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "initiatorId", ignore = true)
    @Mapping(target = "category", ignore = true)
    Event toEvent(NewEventDto dto);

    @Mapping(target = "category", source = "category", qualifiedByName = "category")
    void updateEventMap(UpdateEventUserRequest request, @MappingTarget Event event);

    @Named("category")
    default Category map(Long id) {
        if (id == null) return null;
        Category category = new Category();
        category.setId(id);
        return category;
    }
}