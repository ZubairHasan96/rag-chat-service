package com.assessment.ragchat.service;

import com.assessment.ragchat.dto.SessionDto;
import com.assessment.ragchat.exception.ResourceNotFoundException;
import com.assessment.ragchat.model.Session;
import com.assessment.ragchat.repository.SessionRepository;
import com.assessment.ragchat.util.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private MessageService messageService;

    @InjectMocks
    private SessionService sessionService;

    @BeforeEach
    void setup() {}

    @Test
    void createSession_success() {
        SessionDto request = SessionDto.builder().name("New Session").build();
        Session mockModel = mock(Session.class);
        SessionDto expectedDto = SessionDto.builder().id(1L).name("New Session").favorite(false).build();

        try (MockedStatic<SessionFactory> sf = Mockito.mockStatic(SessionFactory.class)) {
            sf.when(() -> SessionFactory.toModel(eq(request))).thenReturn(mockModel);
            sf.when(() -> SessionFactory.toDto(any(Session.class))).thenReturn(expectedDto);

            when(sessionRepository.save(any(Session.class))).thenReturn(mockModel);

            SessionDto result = sessionService.createSession(request);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("New Session", result.getName());
            verify(sessionRepository, times(1)).save(mockModel);
            sf.verify(() -> SessionFactory.toModel(eq(request)), times(1));
            sf.verify(() -> SessionFactory.toDto(any(Session.class)), times(1));
        }
    }

    @Test
    void renameSession_success() {
        Long id = (Long) 10L;
        String newName = "Renamed";
        Session mockModel = mock(Session.class);
        SessionDto expectedDto = SessionDto.builder().id(id).name(newName).favorite(Boolean.FALSE).build();

        try (MockedStatic<SessionFactory> sf = Mockito.mockStatic(SessionFactory.class)) {
            when(sessionRepository.findById(id)).thenReturn(Optional.of(mockModel));
            when(sessionRepository.save(any(Session.class))).thenReturn(mockModel);
            sf.when(() -> SessionFactory.toDto(eq(mockModel))).thenReturn(expectedDto);

            SessionDto result = sessionService.renameSession(id, newName);

            assertNotNull(result);
            assertEquals(id, result.getId());
            assertEquals(newName, result.getName());
            verify(sessionRepository, times(1)).findById(id);
            verify(sessionRepository, times(1)).save(mockModel);
            sf.verify(() -> SessionFactory.toDto(eq(mockModel)), times(1));
        }
    }

    @Test
    void renameSession_notFound() {
        Long id = (Long) 99L;
        when(sessionRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> sessionService.renameSession(id, "x"));

        assertEquals("Session not found", ex.getMessage());
        verify(sessionRepository, times(1)).findById(id);
        verify(sessionRepository, never()).save(any());
    }

    @Test
    void updateFavorite_success() {
        Long id = (Long) 7L;
        Session mockModel = mock(Session.class);
        SessionDto expectedDto = SessionDto.builder().id(id).name("S").favorite(Boolean.TRUE).build();

        try (MockedStatic<SessionFactory> sf = Mockito.mockStatic(SessionFactory.class)) {
            when(sessionRepository.findById(id)).thenReturn(Optional.of(mockModel));
            when(sessionRepository.save(any(Session.class))).thenReturn(mockModel);
            sf.when(() -> SessionFactory.toDto(eq(mockModel))).thenReturn(expectedDto);

            SessionDto result = sessionService.updateFavorite(id, Boolean.TRUE);

            assertNotNull(result);
            assertTrue(result.getFavorite());
            assertEquals(id, result.getId());
            verify(sessionRepository, times(1)).findById(id);
            verify(sessionRepository, times(1)).save(mockModel);
        }
    }

    @Test
    void updateFavorite_notFound() {
        Long id = (Long) 123L;
        when(sessionRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> sessionService.updateFavorite(id, Boolean.TRUE));

        assertEquals("Session not found", ex.getMessage());
        verify(sessionRepository, times(1)).findById(id);
        verify(sessionRepository, never()).save(any());
    }

    @Test
    void deleteSession_success() {
        Long id = (Long) 5L;
        Session mockModel = mock(Session.class);

        // ensure the mocked session returns the expected id when service reads it
        when(mockModel.getId()).thenReturn(id);

        when(sessionRepository.findById(id)).thenReturn(Optional.of(mockModel));
        doNothing().when(sessionRepository).deleteById(id);

        sessionService.deleteSession(id);

        verify(sessionRepository, times(1)).findById(id);
        verify(sessionRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteSession_notFound() {
        Long id = (Long) 88L;
        when(sessionRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> sessionService.deleteSession(id));

        assertEquals("Session not found", ex.getMessage());
        // use anyLong() for these verifications — don't wrap anyLong() in Long.valueOf(...)
        verify(messageService, never()).deleteAllBySessionId(Long.valueOf(anyLong()));
        verify(sessionRepository, never()).deleteById(Long.valueOf(anyLong()));
    }

    @Test
    void getAllSessions_success() {
        Session s1 = mock(Session.class);
        Session s2 = mock(Session.class);
        SessionDto d1 = SessionDto.builder().id(1L).name("A").favorite(false).build();
        SessionDto d2 = SessionDto.builder().id(2L).name("B").favorite(true).build();

        when(sessionRepository.findAll()).thenReturn(asList(s1, s2));

        try (MockedStatic<SessionFactory> sf = Mockito.mockStatic(SessionFactory.class)) {
            // return d1 then d2 for consecutive toDto calls
            sf.when(() -> SessionFactory.toDto(s1)).thenReturn(d1);
            sf.when(() -> SessionFactory.toDto(s2)).thenReturn(d2);

            List<SessionDto> list = sessionService.getAllSessions();

            assertNotNull(list);
            assertEquals(2, list.size());
            assertEquals("A", list.get(0).getName());
            assertEquals("B", list.get(1).getName());

            verify(sessionRepository, times(1)).findAll();
            sf.verify(() -> SessionFactory.toDto(s1), times(1));
            sf.verify(() -> SessionFactory.toDto(s2), times(1));
        }
    }

    @Test
    void getSessionById_success_and_notFound() {
        Long id = (Long) 42L;
        Session mockModel = mock(Session.class);
        SessionDto expectedDto = SessionDto.builder().id(id).name("Single").favorite(false).build();

        try (MockedStatic<SessionFactory> sf = Mockito.mockStatic(SessionFactory.class)) {
            when(sessionRepository.findById(id)).thenReturn(Optional.of(mockModel));
            sf.when(() -> SessionFactory.toDto(eq(mockModel))).thenReturn(expectedDto);

            SessionDto got = sessionService.getSessionById(id);
            assertNotNull(got);
            assertEquals("Single", got.getName());

            when(sessionRepository.findById(Long.valueOf(999L))).thenReturn(Optional.empty());
            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                    () -> sessionService.getSessionById(Long.valueOf(999L)));
            assertEquals("Session not found", ex.getMessage());
        }
    }
}