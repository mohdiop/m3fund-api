package com.mohdiop.m3fundapi.entity;

import com.mohdiop.m3fundapi.entity.enums.ActionType;
import com.mohdiop.m3fundapi.entity.enums.EntityName;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "actions")
@Builder
public class Action {

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    Project project;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    Payment payment;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ActionType actionType;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EntityName entityName;
    @Column(nullable = false)
    private LocalDateTime actionDate;
    @ManyToOne
    @JoinColumn(nullable = false, name = "author_id")
    private Administrator author;
}
