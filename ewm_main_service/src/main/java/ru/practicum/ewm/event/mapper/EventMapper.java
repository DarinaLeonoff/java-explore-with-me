package ru.practicum.ewm.event.mapper;

import org.mapstruct.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.updateDto.UpdateEventRequest;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.mapper.UserMapper;


@Mapper(componentModel = "spring", uses = {CategoryMapper.class, UserMapper.class}, imports = {java.time.LocalDateTime.class, EventState.class})
public interface EventMapper {

    EventShortDto mapEventToShortDto(Event event);

    EventFullDto mapEventToFullDto(Event event);

    Event mapNewEventToEvent(NewEventDto eventDto);
}
