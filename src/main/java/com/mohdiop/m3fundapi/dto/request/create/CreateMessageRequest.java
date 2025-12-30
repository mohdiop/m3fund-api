package com.mohdiop.m3fundapi.dto.request.create;

import com.mohdiop.m3fundapi.entity.Message;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateMessageRequest(
        @NotBlank(message = "Le contenu du message ne doit pas être vide.")
        String content,
        @NotNull(message = "L'identifiant du destinataire est obligatoire.")
        Long recipientId
) {

    public Message toMessage(){
        return Message.builder()
                .id(null)
                .content(content)
                .sentAt(LocalDateTime.now())
                .build();
    }
}
