package com.assessment.ragchat.util;

import com.assessment.ragchat.dto.SessionDto;
import com.assessment.ragchat.model.Session;

public class SessionFactory {

    public static SessionDto toDto(Session session) {
        if (session == null) {
            return null;
        }
        return SessionDto.builder()
                .id(session.getId())
                .name(session.getName())
                .favorite(session.isFavorite())
                .messages(MessageFactory.toDtoList(session.getMessages()))
                .build();
    }

    public static Session toModel(SessionDto sessionDto) {
        if (sessionDto == null) {
            return null;
        }
        return Session.builder()
                .name(sessionDto.getName())
                .isFavorite(false)
                .build();
    }
}
