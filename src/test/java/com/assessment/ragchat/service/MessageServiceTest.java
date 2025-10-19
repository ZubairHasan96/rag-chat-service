package com.assessment.ragchat.service;

import com.assessment.ragchat.dto.MessageDto;
import com.assessment.ragchat.exception.ResourceNotFoundException;
import com.assessment.ragchat.model.Message;
import com.assessment.ragchat.model.Session;
import com.assessment.ragchat.repository.MessageRepository;
import com.assessment.ragchat.util.MessageFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private SessionService sessionService;

    @InjectMocks
    private MessageService messageService;

    @BeforeEach
    void setUp() {
        // MockitoExtension initializes mocks
    }

    @Test
    void addMessage_success() {
        Long sessionId = 1L;
        Session session = mock(Session.class);
        when(session.getId()).thenReturn(sessionId);
        MessageDto requestDto = MessageDto.builder().sender("user").content("hello").build();

        Message entity = mock(Message.class);
        Message saved = mock(Message.class);
        MessageDto expectedDto = MessageDto.builder().id(10L).sender("user").content("hello").build();

        when(sessionService.findById(sessionId)).thenReturn(session);
        when(messageRepository.save(entity)).thenReturn(saved);

        try (MockedStatic<MessageFactory> mf = Mockito.mockStatic(MessageFactory.class)) {
            mf.when(() -> MessageFactory.toEntity(sessionId, requestDto)).thenReturn(entity);
            mf.when(() -> MessageFactory.toDto(saved)).thenReturn(expectedDto);

            MessageDto result = messageService.addMessage(sessionId, requestDto);

            assertNotNull(result);
            assertEquals(expectedDto.getId(), result.getId());
            assertEquals(expectedDto.getSender(), result.getSender());
            assertEquals(expectedDto.getContent(), result.getContent());

            verify(sessionService, times(1)).findById(sessionId);
            verify(messageRepository, times(1)).save(entity);
            mf.verify(() -> MessageFactory.toEntity(sessionId, requestDto), times(1));
            mf.verify(() -> MessageFactory.toDto(saved), times(1));
        }
    }

    @Test
    void addMessage_sessionNotFound() {
        Long sessionId = 99L;
        MessageDto requestDto = MessageDto.builder().sender("user").content("hello").build();

        when(sessionService.findById(sessionId)).thenThrow(new ResourceNotFoundException("Session not found"));

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> messageService.addMessage(sessionId, requestDto));

        assertEquals("Session not found", ex.getMessage());
        verify(messageRepository, never()).save(any());
    }

    @Test
    void getMessagesBySessionId_success() {
        Long sessionId = 2L;
        Session session = mock(Session.class);
        when(session.getId()).thenReturn(sessionId);
        Message m1 = mock(Message.class);
        List<Message> messages = List.of(m1);
        MessageDto dto1 = MessageDto.builder().id(1L).sender("a").content("m1").build();

        when(sessionService.findById(sessionId)).thenReturn(session);
        when(messageRepository.findBySession_Id(sessionId)).thenReturn(messages);

        try (MockedStatic<MessageFactory> mf = Mockito.mockStatic(MessageFactory.class)) {
            mf.when(() -> MessageFactory.toDtoList(messages)).thenReturn(singletonList(dto1));

            List<MessageDto> result = messageService.getMessagesBySessionId(sessionId);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(dto1.getId(), result.get(0).getId());

            verify(sessionService, times(1)).findById(sessionId);
            verify(messageRepository, times(1)).findBySession_Id(sessionId);
            mf.verify(() -> MessageFactory.toDtoList(messages), times(1));
        }
    }

    @Test
    void getMessagesBySessionId_sessionNotFound() {
        Long sessionId = 123L;
        when(sessionService.findById(sessionId)).thenThrow(new ResourceNotFoundException("Session not found"));

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> messageService.getMessagesBySessionId(sessionId));

        assertEquals("Session not found", ex.getMessage());
        verify(messageRepository, never()).findBySession_Id(anyLong());
    }

    @Test
    void deleteAllBySessionId_callsRepository() {
        Long sessionId = 5L;
        doNothing().when(messageRepository).deleteAllBySession_Id(sessionId);

        messageService.deleteAllBySessionId(sessionId);

        verify(messageRepository, times(1)).deleteAllBySession_Id(sessionId);
    }
}