package com.mohdiop.m3fundapi.repository;

import com.mohdiop.m3fundapi.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
