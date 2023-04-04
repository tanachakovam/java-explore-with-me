package ru.practicum.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ViewStats;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository extends JpaRepository<EndpointHit, Long> {

    @Query("select new ru.practicum.ViewStats(e.app, e.uri, count(distinct e.ip)) " +
            "from EndpointHit e " +
            "where e.timestamp >= :from " +
            "and e.timestamp <= :to " +
            "and e.uri in (:uris) " +
            "group by e.app, e.uri " +
            "order by count (distinct e.ip) DESC")
    List<ViewStats> getUniqueStats(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to, @Param("uris") List<String> uris);

    @Query("select new ru.practicum.ViewStats(e.app, e.uri, count(distinct e.ip)) " +
            "from EndpointHit e " +
            "where e.timestamp >= :from " +
            "and e.timestamp <= :to " +
            "group by e.app, e.uri " +
            "order by count (distinct e.ip) DESC")
    List<ViewStats> getUniqueStatsWithoutUris(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("select new ru.practicum.ViewStats(e.app, e.uri, count(e.ip)) " +
            "from EndpointHit e " +
            "where e.timestamp >= :from " +
            "and e.timestamp <= :to " +
            "and e.uri in (:uris) " +
            "group by e.app, e.uri " +
            "order by count(e.ip) DESC")
    List<ViewStats> getStats(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to, @Param("uris") List<String> uris);

    @Query("select new ru.practicum.ViewStats(e.app, e.uri, count(e.ip)) " +
            "from EndpointHit e " +
            "where e.timestamp >= :from " +
            "and e.timestamp <= :to " +
            "group by e.app, e.uri " +
            "order by count(e.ip) DESC")
    List<ViewStats> getStatsWithoutUris(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
