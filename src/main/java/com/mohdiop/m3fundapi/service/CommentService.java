package com.mohdiop.m3fundapi.service;

import com.mohdiop.m3fundapi.dto.request.create.CreateCommentRequest;
import com.mohdiop.m3fundapi.dto.response.CommentResponse;
import com.mohdiop.m3fundapi.repository.CommentRepository;
import com.mohdiop.m3fundapi.repository.ProjectRepository;
import com.mohdiop.m3fundapi.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public CommentService(CommentRepository commentRepository, UserRepository userRepository, ProjectRepository projectRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }

    public CommentResponse createComment(
            Long userId,
            Long projectId,
            CreateCommentRequest createCommentRequest
    ) {
        var user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Utilisateur introuvable.")
                );
        var project = projectRepository.findById(projectId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Projet introuvable.")
                );
        var comment = createCommentRequest.toComment();
        if(createCommentRequest.isResponse()) {
            var pc = commentRepository.findById(createCommentRequest.parentId())
                    .orElseThrow(
                            () -> new EntityNotFoundException("Commentaire parent introuvable.")
                    );
            comment.setParentComment(pc);
        }
        comment.setProject(project);
        comment.setUser(user);
        return commentRepository.save(comment).toResponse();
    }
}
