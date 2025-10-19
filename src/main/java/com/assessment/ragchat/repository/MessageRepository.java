package com.assessment.ragchat.repository;

import com.assessment.ragchat.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySession_Id(long sessionId);
    void deleteAllBySession_Id(long sessionId);
}