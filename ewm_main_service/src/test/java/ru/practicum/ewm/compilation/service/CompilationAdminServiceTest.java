package ru.practicum.ewm.compilation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationDto;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.notFound.CompilationNotFound;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CompilationAdminServiceTest {

    @Mock
    private CompilationMapper mapper;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private CompilationRepository compilationRepository;

    @InjectMocks
    private CompilationAdminServiceImpl service;

    @Test
    void shouldCreateCompilation() {
        NewCompilationDto dto = new NewCompilationDto();
        dto.setEvents(List.of(1L, 2L));

        Compilation entity = new Compilation();
        Compilation saved = new Compilation();
        CompilationDto resultDto = new CompilationDto();

        Event e1 = new Event();
        Event e2 = new Event();

        when(mapper.mapNewCompilationToEntity(dto)).thenReturn(entity);
        when(eventRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(e1, e2));
        when(compilationRepository.save(entity)).thenReturn(saved);
        when(mapper.mapEntityToDto(saved)).thenReturn(resultDto);

        CompilationDto result = service.createCompilation(dto);

        assertEquals(resultDto, result);
        verify(eventRepository).findAllById(List.of(1L, 2L));
    }

    @Test
    void shouldCreateCompilationWithoutEvents() {
        NewCompilationDto dto = new NewCompilationDto();
        dto.setEvents(List.of());

        Compilation entity = new Compilation();
        Compilation saved = new Compilation();

        when(mapper.mapNewCompilationToEntity(dto)).thenReturn(entity);
        when(compilationRepository.save(entity)).thenReturn(saved);
        when(mapper.mapEntityToDto(saved)).thenReturn(new CompilationDto());

        service.createCompilation(dto);

        verify(eventRepository, never()).findAllById(any());
    }

    @Test
    void shouldHandleNullEvents() {
        NewCompilationDto dto = new NewCompilationDto();
        dto.setEvents(null);

        Compilation entity = new Compilation();

        when(mapper.mapNewCompilationToEntity(dto)).thenReturn(entity);
        when(compilationRepository.save(entity)).thenReturn(entity);
        when(mapper.mapEntityToDto(any())).thenReturn(new CompilationDto());

        service.createCompilation(dto);

        verify(eventRepository, never()).findAllById(any());
    }

    @Test
    void shouldUpdateCompilation() {
        long id = 1L;

        UpdateCompilationDto dto = new UpdateCompilationDto();

        Compilation oldComp = new Compilation();
        Compilation updated = new Compilation();
        Compilation saved = new Compilation();
        CompilationDto result = new CompilationDto();

        when(compilationRepository.findById(id)).thenReturn(Optional.of(oldComp));
        when(mapper.updateCompilation(oldComp, dto)).thenReturn(updated);
        when(compilationRepository.save(updated)).thenReturn(saved);
        when(mapper.mapEntityToDto(saved)).thenReturn(result);

        CompilationDto response = service.updateCompilation(id, dto);

        assertEquals(result, response);
    }

    @Test
    void shouldUpdateCompilationWithEventsNull() {
        long id = 1L;

        UpdateCompilationDto dto = new UpdateCompilationDto();
        dto.setEvents(null);

        Compilation oldComp = new Compilation();
        Compilation updated = new Compilation();
        Compilation saved = new Compilation();
        CompilationDto result = new CompilationDto();

        when(compilationRepository.findById(id)).thenReturn(Optional.of(oldComp));
        when(mapper.updateCompilation(oldComp, dto)).thenReturn(updated);
        when(compilationRepository.save(updated)).thenReturn(saved);
        when(mapper.mapEntityToDto(saved)).thenReturn(result);

        CompilationDto response = service.updateCompilation(id, dto);

        assertEquals(result, response);
    }

    @Test
    void shouldUpdateCompilationWithEventsEmpty() {
        long id = 1L;

        UpdateCompilationDto dto = new UpdateCompilationDto();
        dto.setEvents(List.of());

        Compilation oldComp = new Compilation();
        Compilation updated = new Compilation();
        Compilation saved = new Compilation();
        CompilationDto result = new CompilationDto();

        when(compilationRepository.findById(id)).thenReturn(Optional.of(oldComp));
        when(mapper.updateCompilation(oldComp, dto)).thenReturn(updated);
        when(compilationRepository.save(updated)).thenReturn(saved);
        when(mapper.mapEntityToDto(saved)).thenReturn(result);

        CompilationDto response = service.updateCompilation(id, dto);

        assertEquals(result, response);
    }

    @Test
    void shouldUpdateCompilationWithEvents() {
        long id = 1L;

        UpdateCompilationDto dto = new UpdateCompilationDto();
        dto.setEvents(List.of(1L, 2L));

        Compilation oldComp = new Compilation();
        Compilation updated = new Compilation();
        Compilation saved = new Compilation();
        CompilationDto result = new CompilationDto();

        when(compilationRepository.findById(id)).thenReturn(Optional.of(oldComp));
        when(mapper.updateCompilation(oldComp, dto)).thenReturn(updated);
        when(compilationRepository.save(updated)).thenReturn(saved);
        when(mapper.mapEntityToDto(saved)).thenReturn(result);

        CompilationDto response = service.updateCompilation(id, dto);

        assertEquals(result, response);
    }


    @Test
    void shouldThrowWhenCompilationNotFound() {
        when(compilationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CompilationNotFound.class, () -> service.updateCompilation(1L, new UpdateCompilationDto()));
    }

    @Test
    void shouldDeleteCompilation() {
        service.removeCompilation(10L);

        verify(compilationRepository).deleteById(10L);
    }


}
