package com.assessment.ragchat.util;

import com.assessment.ragchat.dto.MessageDto;
import com.assessment.ragchat.model.Message;
import com.assessment.ragchat.model.Session;

import java.util.List;
import java.util.stream.Collectors;

public class MessageFactory {

    public static MessageDto toDto(Message message) {
        if (message == null) {
            return null;
        }
        return MessageDto.builder()
                .id(message.getId())
                .sender(message.getSender())
                .content(message.getContent())
                .context(message.getContext())
                .sessionId(message.getSession().getId())
                .build();
    }

    public static List<MessageDto> toDtoList(List<Message> messages) {
        if (messages == null) {
            return null;
        }
        return messages.stream()
                .map(MessageFactory::toDto)
                .collect(Collectors.toList());
    }

    public static Message toEntity(Long sessionId, MessageDto messageDto) {
        return Message.builder()
                .id(messageDto.getId())
                .content(messageDto.getContent())
                .sender(messageDto.getSender())
                .session(Session.builder().id(sessionId).build())
                .build();
    }

}
