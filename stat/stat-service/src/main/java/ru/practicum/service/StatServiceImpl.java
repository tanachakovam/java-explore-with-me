package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.EndpointHitDto;
import ru.practicum.ValidateException;
import ru.practicum.ViewStats;
import ru.practicum.mapper.StatMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {
    private final StatMapper statMapper;
    private final StatRepository statRepository;

    @Override
    @Transactional
    public EndpointHitDto postHit(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = statMapper.toEndpointHit(endpointHitDto);
        EndpointHit postedHit = statRepository.save(endpointHit);
        return statMapper.toEndpointHitDto(postedHit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (end.isBefore(start)) {
            throw new ValidateException("End date can't be before start date");
        }
        List<ViewStats> stats;
        if (unique) {
            stats = statRepository.getUniqueStats(start, end, uris);
        } else {
            stats = statRepository.getStats(start, end, uris);
        }
        if (uris == null) {
            stats = statRepository.getFullStats(start, end);
        }
        return stats;
    }
}
