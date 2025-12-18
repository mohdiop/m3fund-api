package com.mohdiop.m3fundapi.entity;

import com.mohdiop.m3fundapi.dto.response.CommentResponse;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "comments")
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private LocalDateTime date;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_id")
    private Comment parentComment;

    @Column(nullable = false, columnDefinition = "BOOL DEFAULT FALSE")
    private boolean isResponse;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false, name = "project_id")
    private Project project;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false, name = "author_id ")
    private User user;

    public CommentResponse toResponse() {
        return new CommentResponse(
                id,
                content,
                isResponse,
                date,
                isResponse ? parentComment.toResponse() : null,
                project.getId(),
                user.getId()
        );
    }
}
