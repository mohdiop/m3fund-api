package com.mohdiop.m3fundapi.dto.response;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        String content,
        boolean isResponse,
        LocalDateTime date,
        CommentResponse parentComment,
        Long projectId,
        Long userId
) {
}
