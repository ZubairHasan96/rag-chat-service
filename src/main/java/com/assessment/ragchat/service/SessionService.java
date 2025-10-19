package com.assessment.ragchat.service;

import com.assessment.ragchat.exception.ResourceNotFoundException;
import com.assessment.ragchat.model.Session;
import com.assessment.ragchat.repository.SessionRepository;
import com.assessment.ragchat.dto.SessionDto;
import com.assessment.ragchat.util.SessionFactory;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;

@Service
@AllArgsConstructor
public class SessionService {


    private final SessionRepository sessionRepository;

    private static final Logger log = LoggerFactory.getLogger(SessionService.class);


    public SessionDto createSession(SessionDto sessionDto) {
        Session sessionToSave = SessionFactory.toModel(sessionDto);
        Session savedSession = sessionRepository.save(sessionToSave);
        return SessionFactory.toDto(savedSession);
    }

    public SessionDto renameSession(Long sessionId, String newName) {
        Session session = findById(sessionId);
        session.setName(newName);
        Session updatedSession = sessionRepository.save(session);
        return SessionFactory.toDto(updatedSession);
    }

    public SessionDto updateFavorite(Long sessionId, Boolean isFavorite) {
        Session session = findById(sessionId);
        session.setFavorite(isFavorite != null && isFavorite);
        return SessionFactory.toDto(sessionRepository.save(session));
    }

    public void deleteSession(Long sessionId) {
        Session session = findById(sessionId);
        sessionRepository.deleteById(session.getId());
    }

    public List<SessionDto> getAllSessions() {
        List<Session> sessions = sessionRepository.findAll();
        return sessions.stream().map(SessionFactory::toDto).toList();
    }

    public SessionDto getSessionById(Long sessionId) {
        Session session = findById(sessionId);
        return SessionFactory.toDto(session);
    }

    public Session findById(Long sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(getSessionNotFoundExceptionWithLog(sessionId));
    }

    private static Supplier<ResourceNotFoundException> getSessionNotFoundExceptionWithLog(Long sessionId) {
        return () -> {
            log.error("Session not found for id = {}", sessionId);
            return new ResourceNotFoundException("Session not found");
        };
    }
}