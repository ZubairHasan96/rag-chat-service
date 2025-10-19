package com.assessment.ragchat.controller;

import com.assessment.ragchat.dto.SessionDto;
import com.assessment.ragchat.exception.InvalidInputException;
import com.assessment.ragchat.exception.ResourceNotFoundException;
import com.assessment.ragchat.service.SessionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SessionControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private SessionService sessionService;

    @InjectMocks
    private SessionController sessionController;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        // configure validator so @Valid on controller works in standalone setup
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        // include GlobalExceptionHandler so exception mappings are exercised
        mockMvc = MockMvcBuilders.standaloneSetup(sessionController)
                .setValidator(validator)
                .setControllerAdvice(new com.assessment.ragchat.exception.GlobalExceptionHandler())
                .build();
    }

    @Test
    void createSession_success() throws Exception {
        SessionDto req = new SessionDto(null, "Test Session", false, null);
        SessionDto res = new SessionDto(1L, "Test Session", false, null);

        when(sessionService.createSession(ArgumentMatchers.any(SessionDto.class))).thenReturn(res);

        mockMvc.perform(post("/api/session")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Session"))
                .andExpect(jsonPath("$.favorite").value(false));
    }

    @Test
    void createSession_validationFailure() throws Exception {
        SessionDto req = new SessionDto(null, "", false, null);

        mockMvc.perform(post("/api/session")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createSession_serviceThrowsInvalidInput() throws Exception {
        SessionDto req = new SessionDto(null, "Valid Name", false, null);

        when(sessionService.createSession(ArgumentMatchers.any(SessionDto.class)))
                .thenThrow(new InvalidInputException("Invalid session data"));

        mockMvc.perform(post("/api/session")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid session data"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void renameSession_success() throws Exception {
        Long id = 1L;
        SessionDto req = new SessionDto(null, "New Name", false, null);
        SessionDto res = new SessionDto(id, "New Name", false, null);

        when(sessionService.renameSession(id, "New Name")).thenReturn(res);

        mockMvc.perform(patch("/api/session/{id}/rename", id)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.intValue()))
                .andExpect(jsonPath("$.name").value("New Name"));
    }

    @Test
    void renameSession_notFound() throws Exception {
        Long id = 11L;
        SessionDto req = new SessionDto(null, "New Name", false, null);

        when(sessionService.renameSession(id, "New Name"))
                .thenThrow(new ResourceNotFoundException("Session not found"));

        mockMvc.perform(patch("/api/session/{id}/rename", id)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Session not found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void updateFavorite_success() throws Exception {
        Long id = 2L;
        SessionDto req = new SessionDto(null, null, true, null);
        SessionDto res = new SessionDto(id, "Some", true, null);

        when(sessionService.updateFavorite(id, true)).thenReturn(res);

        mockMvc.perform(patch("/api/session/{id}/favorite", id)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.intValue()))
                .andExpect(jsonPath("$.favorite").value(true));
    }

    @Test
    void updateFavorite_notFound() throws Exception {
        Long id = 22L;
        SessionDto req = new SessionDto(null, null, true, null);

        when(sessionService.updateFavorite(id, true))
                .thenThrow(new ResourceNotFoundException("Session not found"));

        mockMvc.perform(patch("/api/session/{id}/favorite", id)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Session not found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deleteSession_success() throws Exception {
        Long id = 3L;
        doNothing().when(sessionService).deleteSession(id);

        mockMvc.perform(delete("/api/session/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    void deleteSession_notFound() throws Exception {
        Long id = 33L;
        doThrow(new ResourceNotFoundException("Session not found")).when(sessionService).deleteSession(id);

        mockMvc.perform(delete("/api/session/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Session not found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void getAllSessions_success() throws Exception {
        SessionDto s1 = new SessionDto(1L, "S1", false, null);
        SessionDto s2 = new SessionDto(2L, "S2", true, null);

        when(sessionService.getAllSessions()).thenReturn(List.of(s1, s2));

        mockMvc.perform(get("/api/session"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void getSessionById_success() throws Exception {
        Long id = 5L;
        SessionDto res = new SessionDto(id, "Found", false, null);

        when(sessionService.getSessionById(id)).thenReturn(res);

        mockMvc.perform(get("/api/session/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.intValue()))
                .andExpect(jsonPath("$.name").value("Found"));
    }

    @Test
    void getSessionById_notFound() throws Exception {
        Long id = 55L;
        when(sessionService.getSessionById(id)).thenThrow(new ResourceNotFoundException("Session not found"));

        mockMvc.perform(get("/api/session/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Session not found"))
                .andExpect(jsonPath("$.status").value(404));
    }
}