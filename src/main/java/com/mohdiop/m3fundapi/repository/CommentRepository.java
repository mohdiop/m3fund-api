package com.mohdiop.m3fundapi.repository;

import com.mohdiop.m3fundapi.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Long, Comment> {
}
