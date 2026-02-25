package ru.practicum.ewm.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.model.Request;

@Mapper(componentModel = "spring")
public interface RequestMapper {

    @Mapping(target = "requester", source = "request.user.id")
    @Mapping(target = "event", source = "request.event.id")
    ParticipationRequestDto mapRequestToDto(Request request);
}
