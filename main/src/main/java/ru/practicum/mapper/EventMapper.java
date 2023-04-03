package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.model.Event;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(source = "category", target = "category.id")
    Event toEvent(NewEventDto newEventDto);


    EventFullDto toEventFullDto(Event event);



    List<EventShortDto> toEventShortDto(Collection<Event> event);

    EventShortDto toEventShortDto(Event event);

    List<EventFullDto> toEventFullDto(Collection<Event> event);
}
