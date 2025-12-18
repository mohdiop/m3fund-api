package com.mohdiop.m3fundapi.dto.request.create;

import com.mohdiop.m3fundapi.entity.Comment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateCommentRequest(
        @NotBlank(message = "Le commentaire ne peut pas être vide.") String content,
        @NotNull(message = "Est-ce une réponse") boolean isResponse,
        Long parentId
) {

    public Comment toComment() {
        return Comment.builder()
                .id(null)
                .date(LocalDateTime.now())
                .content(content)
                .isResponse(isResponse)
                .build();
    }
}
