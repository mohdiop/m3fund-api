package com.mohdiop.m3fundapi.repository;

import com.mohdiop.m3fundapi.entity.Administrator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AdministratorRepository extends JpaRepository<Administrator, Long> {
    List<Administrator> findByEmailOrPhone(String email, String phone);

    Optional<Administrator> findByEmail(String email);

    Optional<Administrator> findByPhone(String phone);
}
