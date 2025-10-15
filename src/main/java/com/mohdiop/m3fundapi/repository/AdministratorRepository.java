package com.mohdiop.m3fundapi.repository;

import com.mohdiop.m3fundapi.entity.Administrator;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdministratorRepository extends JpaRepository<Administrator, Long> {
}
