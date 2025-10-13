package com.mohdiop.m3fundapi.repository;

import com.mohdiop.m3fundapi.entity.Action;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActionRepository extends JpaRepository<Long, Action> {
}
