package ru.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.practicum.model.enums.EventState;


import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "event_id")
    private Long event;
    private LocalDateTime created;
    @Column(name = "requester_id")
    private Long requester;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private EventState status;
}
