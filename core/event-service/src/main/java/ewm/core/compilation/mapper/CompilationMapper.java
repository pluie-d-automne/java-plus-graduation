package ewm.core.compilation.mapper;

import ewm.core.compilation.dto.CompilationDto;
import ewm.core.compilation.dto.NewCompilationDto;
import ewm.core.compilation.dto.UpdateCompilationDto;
import ewm.core.compilation.model.Compilation;
import ewm.core.event.mapper.EventMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", uses = {EventMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CompilationMapper {

    CompilationDto compilationToDto(Compilation compilation);

    @Mapping(target = "pinned", defaultExpression  = "java(false)")
    @Mapping(target = "events", ignore = true)
    Compilation postDtoToCompilation(NewCompilationDto newCompilationDto);

    @Mapping(target = "events", ignore = true)
    void updateDtoToCompilation(@MappingTarget Compilation compilation, UpdateCompilationDto dto);
}
