package com.mohdiop.m3fundapi.entity;

import com.mohdiop.m3fundapi.entity.enums.ActionType;
import com.mohdiop.m3fundapi.entity.enums.EntityName;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "actions")
public class Action {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

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
