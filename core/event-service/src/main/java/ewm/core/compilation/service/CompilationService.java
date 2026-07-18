package ewm.core.compilation.service;

import ewm.core.compilation.dto.CompilationDto;
import ewm.core.compilation.dto.NewCompilationDto;
import ewm.core.compilation.dto.UpdateCompilationDto;

import java.util.List;

public interface CompilationService {

    CompilationDto create(NewCompilationDto newCompilationDto);

    void delete(Long compilationId);

    CompilationDto update(UpdateCompilationDto newCompilationDto, Long compilationId);

    List<CompilationDto> getCompilations(boolean pinned, Integer from, Integer size);

    CompilationDto getCompilation(Long compId);
}
