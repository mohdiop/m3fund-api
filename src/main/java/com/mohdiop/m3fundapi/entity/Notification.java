package com.mohdiop.m3fundapi.entity;

import com.mohdiop.m3fundapi.entity.enums.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @Enumerated(EnumType.STRING)
    private NotificationType type;
}
