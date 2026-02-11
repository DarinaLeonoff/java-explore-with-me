package ru.practicum.ewm.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.mapper.UserMapper;



@Mapper(componentModel = "spring",
        uses = {CategoryMapper.class, UserMapper.class},
        imports = { java.time.LocalDateTime.class, EventState.class })
public interface EventMapper {

    EventShortDto mapEventToShortDto(Event event);

    EventFullDto mapEventToFullDto(Event event);

    @Mapping(source = "dto", target = "category")
    @Mapping(source = "userDto", target = "initiator")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdOn", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "state", expression = "java(EventState.PENDING)")
    @Mapping(target = "views", expression = "java(0)")
    Event mapNewEventToEvent(NewEventDto eventDto, CategoryDto dto, UserDto userDto);

}
