package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.model.Request;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RequestMapper {

    ParticipationRequestDto toRequestDto(Request request);

    List<ParticipationRequestDto> toRequestDto(List<Request> request);


}
