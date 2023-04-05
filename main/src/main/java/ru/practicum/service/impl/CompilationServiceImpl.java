package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.request.UpdateCompilationRequest;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.repository.EventRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.service.CompilationService;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationMapper mapper;
    private final CompilationRepository repository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto saveCompilation(NewCompilationDto newCompilationDto) {
        List<Event> events = eventRepository.findAllByIdIn(newCompilationDto.getEvents());
        Compilation compilation = mapper.toCompilation(newCompilationDto);
        compilation.setEvents(new HashSet<>(events));

        return mapper.toCompilationDto(repository.save(compilation));
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        findCompilationById(compId);
        repository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest compilationDto) {
        Compilation compilationToUpdate = findCompilationById(compId);

       mapper.update(compilationDto, compilationToUpdate);

        if (compilationDto.getEvents() != null) {
            List<Event> events = eventRepository.findAllByIdIn(compilationDto.getEvents());
            compilationToUpdate.setEvents(new HashSet<>(events));
        }
        repository.save(compilationToUpdate);
        return mapper.toCompilationDto(compilationToUpdate);
    }

    @Transactional(readOnly = true)
    public Compilation findCompilationById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Compilation " + id + " is not found."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getCompilations(Boolean pinned, Pageable pageable) {
        return mapper.toCompilationDto(repository.findAllByPinnedIs(pinned, pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getCompilation(Long compId) {
        return mapper.toCompilationDto(findCompilationById(compId));
    }
}

