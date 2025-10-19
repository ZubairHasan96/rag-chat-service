package com.assessment.ragchat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.assessment.ragchat.dto.MessageDto;
import com.assessment.ragchat.service.MessageService;

import java.util.List;

@RestController
@RequestMapping("/api/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/session/{sessionId}")
    public MessageDto addMessage(@PathVariable Long sessionId, @RequestBody MessageDto messageDto) {
        return messageService.addMessage(sessionId, messageDto);
    }

    @GetMapping("/session/{sessionId}")
    public List<MessageDto> getMessages(@PathVariable Long sessionId) {
        return messageService.getMessagesBySessionId(sessionId);
    }
}