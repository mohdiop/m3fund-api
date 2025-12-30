package com.mohdiop.m3fundapi.dto.response;

import java.time.LocalDateTime;

public record MessageResponse (
        Long id,
        String content,
        LocalDateTime sentAt,
        Long discussionId
){
}
