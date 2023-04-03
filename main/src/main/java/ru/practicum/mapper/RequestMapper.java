package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.model.Request;
import ru.practicum.dto.request.ParticipationRequestDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RequestMapper {

    ParticipationRequestDto toRequestDto(Request request);

    List<ParticipationRequestDto> toRequestDto(List<Request> request);


}
