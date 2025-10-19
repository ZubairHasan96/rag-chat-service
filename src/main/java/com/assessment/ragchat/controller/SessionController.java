package com.assessment.ragchat.controller;

import com.assessment.ragchat.dto.SessionDto;
import com.assessment.ragchat.service.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/session")
public class SessionController {

    private static final Logger log = LoggerFactory.getLogger(SessionController.class);
    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping
    public SessionDto createSession(@Valid @RequestBody SessionDto sessionDto) {
        log.debug("createSession request payload: {}", sessionDto);
        return sessionService.createSession(sessionDto);
    }

    @PatchMapping("/{id}/rename")
    public SessionDto renameSession(@PathVariable Long id, @RequestBody SessionDto sessionDto) {
        log.debug("renameSession id={} newName={}", id, sessionDto.getName());
        return sessionService.renameSession(id, sessionDto.getName());
    }

    @PatchMapping("/{id}/favorite")
    public SessionDto updateFavorite(@PathVariable Long id, @RequestBody SessionDto sessionDto) {
        log.debug("markAsFavorite id={}", id);
        return sessionService.updateFavorite(id, sessionDto.getFavorite());
    }

    @DeleteMapping("/{id}")
    public void deleteSession(@PathVariable Long id) {
        log.debug("deleteSession id={}", id);
        sessionService.deleteSession(id);
    }

    @GetMapping
    public List<SessionDto> getAllSessions() {
        return sessionService.getAllSessions();
    }

    @GetMapping("/{id}")
    public SessionDto getSession(@PathVariable Long id) {
        log.debug("getSession id={}", id);
        return sessionService.getSessionById(id);
    }
}