package com.assessment.ragchat.controller;

import com.assessment.ragchat.dto.MessageDto;
import com.assessment.ragchat.exception.ResourceNotFoundException;
import com.assessment.ragchat.service.MessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MessageControllerTest {

    @Mock
    private MessageService messageService;

    @InjectMocks
    private MessageController messageController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(messageController)
                .setControllerAdvice(new com.assessment.ragchat.exception.GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void addMessage_success() throws Exception {
        Long sessionId = 1L;
        MessageDto request = MessageDto.builder()
                .sender("user")
                .content("hello")
                .build();

        MessageDto response = MessageDto.builder()
                .id(10L)
                .sender("user")
                .content("hello")
                .build();

        when(messageService.addMessage(eq(sessionId), org.mockito.ArgumentMatchers.any(MessageDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/message/session/{sessionId}", sessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.sender").value("user"))
                .andExpect(jsonPath("$.content").value("hello"));
    }

    @Test
    void addMessage_sessionNotFound() throws Exception {
        Long sessionId = 99L;
        MessageDto request = MessageDto.builder()
                .sender("user")
                .content("hello")
                .build();

        when(messageService.addMessage(eq(sessionId), org.mockito.ArgumentMatchers.any(MessageDto.class)))
                .thenThrow(new ResourceNotFoundException("Session not found"));

        mockMvc.perform(post("/api/message/session/{sessionId}", sessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getMessages_success() throws Exception {
        Long sessionId = 2L;
        MessageDto m1 = MessageDto.builder().id(1L).sender("a").content("m1").build();
        MessageDto m2 = MessageDto.builder().id(2L).sender("b").content("m2").build();

        when(messageService.getMessagesBySessionId(sessionId)).thenReturn(List.of(m1, m2));

        mockMvc.perform(get("/api/message/session/{sessionId}", sessionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void getMessages_sessionNotFound() throws Exception {
        Long sessionId = 123L;
        when(messageService.getMessagesBySessionId(sessionId))
                .thenThrow(new ResourceNotFoundException("Session not found"));

        mockMvc.perform(get("/api/message/session/{sessionId}", sessionId))
                .andExpect(status().isNotFound());
    }
}