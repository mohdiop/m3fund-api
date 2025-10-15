package com.mohdiop.m3fundapi.dto.request.create;

import com.mohdiop.m3fundapi.entity.Administrator;
import com.mohdiop.m3fundapi.entity.enums.UserRole;
import com.mohdiop.m3fundapi.entity.enums.UserState;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.Set;

public record CreateAdministratorRequest(
        String firstName,
        String lastName,
        String email,
        String phone,
        String password,
        Set<UserRole> userRoles
) {
    public Administrator toAdministrator() {
        return Administrator.builder()
                .id(null)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phone(phone)
                .password(BCrypt.hashpw(password, BCrypt.gensalt()))
                .userRoles(userRoles)
                .userCreatedAt(LocalDateTime.now())
                .state(UserState.ACTIVE)
                .build();
    }
}
