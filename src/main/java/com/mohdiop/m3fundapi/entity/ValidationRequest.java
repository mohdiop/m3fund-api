package com.mohdiop.m3fundapi.entity;

import com.mohdiop.m3fundapi.dto.response.ValidationRequestOwnerResponse;
import com.mohdiop.m3fundapi.dto.response.ValidationRequestProjectResponse;
import com.mohdiop.m3fundapi.entity.enums.EntityName;
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
    @JoinColumn(name = "owner_id")
    private ProjectOwner owner;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(nullable = false, columnDefinition = "ENUM('PROJECT', 'USER') DEFAULT 'USER'")
    @Enumerated(EnumType.STRING)
    private EntityName entity;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ValidationState state;

    @Column(nullable = false)
    private LocalDateTime date;

    public ValidationRequestOwnerResponse toOwnerResponse() {
        return new ValidationRequestOwnerResponse(
                id,
                owner.toSimpleOwnerResponse(),
                date,
                state
        );
    }

    public ValidationRequestProjectResponse toProjectResponse() {
        return new ValidationRequestProjectResponse(
                id,
                project.toResponse(),
                date,
                state,
                project.getOwner().toSimpleOwnerResponse().name()
        );
    }
}
