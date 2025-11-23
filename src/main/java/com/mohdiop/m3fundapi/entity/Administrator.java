package com.mohdiop.m3fundapi.entity;

import com.mohdiop.m3fundapi.dto.response.AdministratorResponse;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "administrators")
@SuperBuilder
public class Administrator extends User {

    private String firstName;

    private String lastName;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "author")
    private Set<Action> actions;

    public AdministratorResponse toResponse() {
        return new AdministratorResponse(
                getId(),
                firstName,
                lastName,
                getEmail(),
                getPhone(),
                getState(),
                getUserRoles(),
                getUserCreatedAt()
        );
    }
}
