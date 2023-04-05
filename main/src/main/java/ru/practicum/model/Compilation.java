package ru.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.practicum.model.Event;

import javax.persistence.*;
import java.util.Set;


@Entity
@Table(name = "compilations")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinTable(name = "event_compilations",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Event> events;
    private Boolean pinned;
    private String title;
}
