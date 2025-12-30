package com.mohdiop.m3fundapi.service;

import com.mohdiop.m3fundapi.dto.request.create.CreateMessageRequest;
import com.mohdiop.m3fundapi.dto.response.MessageResponse;
import com.mohdiop.m3fundapi.entity.Contributor;
import com.mohdiop.m3fundapi.entity.Discussion;
import com.mohdiop.m3fundapi.entity.ProjectOwner;
import com.mohdiop.m3fundapi.entity.enums.UserRole;
import com.mohdiop.m3fundapi.repository.DiscussionRepository;
import com.mohdiop.m3fundapi.repository.MessageRepository;
import com.mohdiop.m3fundapi.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final DiscussionRepository discussionRepository;
    private final UserRepository userRepository;

    public MessageService(MessageRepository messageRepository, DiscussionRepository discussionRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.discussionRepository = discussionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public MessageResponse sendMessage(
            Long userId,
            Long discussionId,
            CreateMessageRequest messageRequest
    ) {
        var user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Utilisateur introuvable.")
                );
        var isOwner = user.getUserRoles().contains(UserRole.ROLE_PROJECT_OWNER);
        var recipient = userRepository.findById(messageRequest.recipientId())
                .orElseThrow(
                        () -> new EntityNotFoundException("Utilisateur introuvable.")
                );
        var message = messageRequest.toMessage();
        Discussion discussion;
        if(discussionRepository.findByContributorIdOrProjectOwnerId(
                !isOwner ? user.getId() : recipient.getId(),
                isOwner ? user.getId() : recipient.getId()
        ).isEmpty()) {
            discussion = new Discussion(
                    null,
                    isOwner ? (ProjectOwner) user : (ProjectOwner) recipient,
                    !isOwner ? (Contributor) user : (Contributor) recipient,
                    new HashSet<>()
            );
            discussion = discussionRepository.save(discussion);
        } else if (user.getUserRoles().contains(UserRole.ROLE_CONTRIBUTOR)) {
            discussion = discussionRepository.findByContributorId(userId)
                    .orElseThrow(
                            () -> new EntityNotFoundException("Utilisateur introuvable.")
                    );
        } else if(user.getUserRoles().contains(UserRole.ROLE_PROJECT_OWNER)) {
            discussion = discussionRepository.findByProjectOwnerId(userId)
                    .orElseThrow(
                            () -> new EntityNotFoundException("Utilisateur introuvable.")
                    );
        } else {
            discussion = null;
        }
        message.setDiscussion(discussion);
        message.setUser(user);
        return messageRepository.save(message).toResponse();
    }
}
