package com.mohdiop.m3fundapi.repository;

import com.mohdiop.m3fundapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
