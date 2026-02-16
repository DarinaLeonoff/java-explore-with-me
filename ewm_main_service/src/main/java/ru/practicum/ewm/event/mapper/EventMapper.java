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

    @Mapping(source = "dto", target = "category")
    @Mapping(source = "userDto", target = "initiator")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdOn", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "state", expression = "java(EventState.PENDING)")
    @Mapping(target = "views", expression = "java(0L)")
    Event mapNewEventToEvent(NewEventDto eventDto, CategoryDto dto, UserDto userDto);

    default Event updateEvent(Event oldEvent,
            UpdateEventRequest updates,
            Category category) {

        if (updates == null) {
            return oldEvent;
        }

        // annotation
        if (updates.getAnnotation() != null) {
            oldEvent.setAnnotation(updates.getAnnotation());
        }

        // description
        if (updates.getDescription() != null) {
            oldEvent.setDescription(updates.getDescription());
        }

        // title
        if (updates.getTitle() != null) {
            oldEvent.setTitle(updates.getTitle());
        }

        // eventDate
        if (updates.getEventDate() != null) {
            oldEvent.setEventDate(updates.getEventDate());
        }

        // category
        if (category != null) {
            oldEvent.setCategory(category);
        }

        // location
        if (updates.getLocation() != null) {
            oldEvent.setLocation(updates.getLocation());
        }

        // paid
        if (updates.getPaid() != null) {
            oldEvent.setPaid(updates.getPaid());
        }

        // participant limit
        if (updates.getParticipantLimit() != null) {
            oldEvent.setParticipantLimit(updates.getParticipantLimit());
        }

        // request moderation
        if (updates.getRequestModeration() != null) {
            oldEvent.setRequestModeration(updates.getRequestModeration());
        }

        return oldEvent;
    }


}
