package com.assessment.ragchat.service;

import com.assessment.ragchat.model.Message;
import com.assessment.ragchat.model.Session;
import com.assessment.ragchat.repository.MessageRepository;
import com.assessment.ragchat.dto.MessageDto;
import com.assessment.ragchat.util.MessageFactory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final SessionService sessionService;


    public MessageDto addMessage(Long sessionId, MessageDto messageDto) {
        Session session = sessionService.findById(sessionId);
        Message message = MessageFactory.toEntity(session.getId(), messageDto);
        Message savedMessage = messageRepository.save(message);
        return MessageFactory.toDto(savedMessage);
    }

    public List<MessageDto> getMessagesBySessionId(Long sessionId) {
        Session session = sessionService.findById(sessionId);
        List<Message> messages = messageRepository.findBySession_Id(session.getId());
        return MessageFactory.toDtoList(messages);
    }

    public void deleteAllBySessionId(Long sessionId) {
        messageRepository.deleteAllBySession_Id(sessionId);
    }


}