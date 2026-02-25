package ru.practicum.ewm.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.user.mapper.UserMapper;


@Mapper(componentModel = "spring", uses = {CategoryMapper.class, UserMapper.class}, imports = {java.time.LocalDateTime.class, EventState.class})
public interface EventMapper {

    EventShortDto mapEventToShortDto(Event event);

    EventFullDto mapEventToFullDto(Event event);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "state", expression = "java(EventState.PENDING)")
    @Mapping(target = "views", expression = "java(0L)")
    @Mapping(target = "createdOn", expression = "java(LocalDateTime.now())")
    Event mapNewEventToEvent(NewEventDto eventDto);
}
