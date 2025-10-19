package com.assessment.ragchat.repository;

import com.assessment.ragchat.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    // Additional query methods can be defined here if needed
}