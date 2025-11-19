package com.mohdiop.m3fundapi.entity;

import com.mohdiop.m3fundapi.dto.response.ValidationRequestResponse;
import com.mohdiop.m3fundapi.entity.enums.ValidationState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "validation_requests")
@Builder
public class ValidationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "owner_id", nullable = false)
    private ProjectOwner owner;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ValidationState state;

    @Column(nullable = false)
    private LocalDateTime date;

    public ValidationRequestResponse toResponse() {
        return new ValidationRequestResponse(
                id,
                owner.toSimpleOwnerResponse(),
                date,
                state
        );
    }
}
