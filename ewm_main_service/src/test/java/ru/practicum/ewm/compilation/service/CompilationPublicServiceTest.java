package ru.practicum.ewm.compilation.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.exception.notFound.CompilationNotFound;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CompilationPublicServiceTest {

        @Mock
        private CompilationRepository repository;

        @Mock
        private CompilationMapper mapper;

        @InjectMocks
        private CompilationPublicServiceImpl service;

        private Compilation compilation;
        private CompilationDto dto;

        @BeforeEach
        void setUp() {
            compilation = new Compilation();
            compilation.setId(1L);

            dto = new CompilationDto();
            dto.setId(1L);
        }

        @Test
        void shouldReturnAllCompilationsWhenPinnedNull() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Compilation> page = new PageImpl<>(List.of(compilation));

            when(repository.findAll(pageable)).thenReturn(page);
            when(mapper.mapEntityToDto(compilation)).thenReturn(dto);

            List<CompilationDto> result = service.getCompilations(null, 0, 10);

            assertEquals(1, result.size());
            verify(repository).findAll(pageable);
            verify(repository, never()).findAllByPinned(any(), any());
        }

        @Test
        void shouldReturnPinnedCompilations() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Compilation> page = new PageImpl<>(List.of(compilation));

            when(repository.findAllByPinned(true, pageable)).thenReturn(page);
            when(mapper.mapEntityToDto(compilation)).thenReturn(dto);

            List<CompilationDto> result = service.getCompilations(true, 0, 10);

            assertEquals(1, result.size());
            verify(repository).findAllByPinned(true, pageable);
            verify(repository, never()).findAll(any(Pageable.class));
        }

        @Test
        void shouldReturnEmptyList() {
            Pageable pageable = PageRequest.of(0, 10);

            when(repository.findAll(pageable)).thenReturn(Page.empty());

            List<CompilationDto> result = service.getCompilations(null, 0, 10);

            assertTrue(result.isEmpty());
        }

        @Test
        void shouldGetCompilationById() {
            when(repository.findById(1L)).thenReturn(Optional.of(compilation));
            when(mapper.mapEntityToDto(compilation)).thenReturn(dto);

            CompilationDto result = service.getCompilationById(1L);

            assertEquals(dto, result);
        }

        @Test
        void shouldThrowExceptionWhenCompilationNotFound() {
            when(repository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(CompilationNotFound.class,
                    () -> service.getCompilationById(1L));
        }

}
