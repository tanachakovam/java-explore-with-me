package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.request.UpdateCompilationRequest;
import ru.practicum.model.Compilation;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompilationMapper {

    CompilationDto toCompilationDto(Compilation compilation);

    @Mapping(target = "events", ignore = true)
    Compilation toCompilation(NewCompilationDto newCompilationDto);


    @Mapping(target = "pinned", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "title", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "events", ignore = true)
    void map(UpdateCompilationRequest updateCompilationRequest, @MappingTarget Compilation compilationToUpdate);


    List<CompilationDto> toCompilationDto(List<Compilation> compilations);
}
