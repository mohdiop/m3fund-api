package com.mohdiop.m3fundapi.repository;

import com.mohdiop.m3fundapi.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
