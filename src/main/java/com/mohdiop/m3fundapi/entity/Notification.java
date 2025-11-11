package com.mohdiop.m3fundapi.entity;

import com.mohdiop.m3fundapi.dto.response.NotificationResponse;
import com.mohdiop.m3fundapi.entity.enums.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "from_user_id")
    private User fromUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "to_user_id")
    private User toUser;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private boolean isRead;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    public NotificationResponse toResponse() {
        String senderName = "";
        var user = Hibernate.unproxy(fromUser);
        switch (user) {
            case Administrator administrator ->
                    senderName = administrator.getFirstName() + " " + administrator.getLastName();
            case Contributor contributor -> senderName = contributor.getFirstName() + " " + contributor.getLastName();
            case ProjectOwner owner -> {
                switch (owner.getType()) {
                    case INDIVIDUAL -> senderName = owner.getFirstName() + " " + owner.getLastName();
                    case ORGANIZATION, ASSOCIATION -> senderName = owner.getEntityName();
                }
            }
            default -> senderName = ((User) user).getEmail();
        }
        return new NotificationResponse(
                id,
                title,
                content,
                senderName,
                date,
                isRead,
                type
        );
    }
}
