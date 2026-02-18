package ru.practicum.ewm.compilation.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationDto;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MapperTest {

    private CompilationMapperImpl mapper = new CompilationMapperImpl();

    Compilation compilation;
    List<Event> events;

    @BeforeEach
    void setup() {
        User initiator = User.builder().id(1L).name("name").email("email").build();
        Category category = Category.builder().id(1).name("cat").build();
        events = List.of(Event.builder().id(1L).createdOn(LocalDateTime.now()).initiator(initiator).title("title").annotation("annot").description("desc").eventDate(LocalDateTime.now().plusDays(3)).category(category).paid(false).participantLimit(10).publishedOn(LocalDateTime.now()).requestModeration(true).confirmedRequests(5).state(EventState.PUBLISHED).location(new Location()).views(2L).build(), Event.builder().id(2L).createdOn(LocalDateTime.now()).initiator(initiator).title("title").annotation("annot").description("desc").eventDate(LocalDateTime.now().plusDays(3)).category(category).paid(false).participantLimit(10).publishedOn(LocalDateTime.now()).requestModeration(true).confirmedRequests(5).state(EventState.PUBLISHED).location(new Location()).views(2L).build());
        compilation = Compilation.builder().id(1L).title("Title").pinned(true).events(events).build();
    }

    @Test
    void newCompilationToEntityConvertTest() {
        NewCompilationDto dto = NewCompilationDto.builder().title("Title").pinned(true).events(List.of(1L, 2L)).build();

        Compilation comp = mapper.mapNewCompilationToEntity(dto);

        assertEquals(dto.getTitle(), comp.getTitle());
        assertEquals(dto.isPinned(), comp.isPinned());
        assertNull(comp.getEvents());
    }

    @Test
    void entityToDtoConvertTest() {


        CompilationDto dto = mapper.mapEntityToDto(compilation);

        assertEquals(compilation.getId(), dto.getId());
        assertEquals(compilation.getTitle(), dto.getTitle());
        assertTrue(dto.isPinned());

        assertTrue(dto.getEvents().contains(events.get(0)));
        assertTrue(dto.getEvents().contains(events.get(1)));
    }

    @Test
    void updateTest() {
        UpdateCompilationDto dto = UpdateCompilationDto.builder().title("new title").build();
        Compilation updated = mapper.updateCompilation(compilation, dto);

        assertEquals(compilation.getId(), updated.getId());
        assertEquals(compilation.isPinned(), updated.isPinned());
        assertEquals(compilation.getEvents(), updated.getEvents());

        assertEquals(dto.getTitle(), updated.getTitle());
    }

    @Test
    void updateTestPinned() {
        UpdateCompilationDto dto = UpdateCompilationDto.builder().pinned(false).build();
        Compilation updated = mapper.updateCompilation(compilation, dto);

        assertEquals(compilation.getId(), updated.getId());
        assertEquals(compilation.isPinned(), updated.isPinned());
        assertEquals(compilation.getEvents(), updated.getEvents());

        assertEquals(dto.getPinned(), updated.isPinned());
    }
}
