package com.mohdiop.m3fundapi.entity;

import com.mohdiop.m3fundapi.dto.response.SystemResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "m3fund")
public class System {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id = 1;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String version;

    @Column(nullable = false)
    private double fund;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public SystemResponse toResponse() {
        return new SystemResponse(
                name,
                version,
                fund,
                createdAt
        );
    }
}
