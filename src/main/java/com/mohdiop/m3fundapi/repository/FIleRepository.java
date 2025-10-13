package com.mohdiop.m3fundapi.repository;

import com.mohdiop.m3fundapi.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FIleRepository extends JpaRepository<Long, File> {
}
