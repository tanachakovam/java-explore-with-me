package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ViewStats;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository extends JpaRepository<EndpointHit, Integer> {

    @Query("select new ru.practicum.ViewStats(e.app, e.uri, count(distinct e.ip)) " +
            " from EndpointHit e " +
            "where e.timestamp >= ?1 " +
            "and e.timestamp <= ?2 " +
            "and e.uri in ?3  " +
            "group by e.app, e.uri " +
            "order by count (distinct e.ip) DESC")
    List<ViewStats> getUniqueStats(LocalDateTime from, LocalDateTime to, List<String> uris);


    @Query("select new ru.practicum.ViewStats(e.app, e.uri, count(e.ip)) " +
            " from EndpointHit e " +
            "where e.timestamp >= ?1 " +
            "and e.timestamp <= ?2 " +
            "and e.uri in ?3  " +
            "group by e.app, e.uri " +
            "order by count(e.ip) DESC")
    List<ViewStats> getStats(LocalDateTime from, LocalDateTime to, List<String> uris);

    @Query("select new ru.practicum.ViewStats(e.app, e.uri, count(e.ip)) " +
            " from EndpointHit e " +
            "where e.timestamp >= ?1 " +
            "and e.timestamp <= ?2 " +
            "group by e.app, e.uri " +
            "order by count(e.ip) DESC")
    List<ViewStats> getFullStats(LocalDateTime from, LocalDateTime to);
}
