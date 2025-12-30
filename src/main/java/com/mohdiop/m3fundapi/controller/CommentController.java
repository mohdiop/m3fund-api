package com.mohdiop.m3fundapi.controller;

import com.mohdiop.m3fundapi.dto.request.create.CreateCommentRequest;
import com.mohdiop.m3fundapi.dto.response.CommentResponse;
import com.mohdiop.m3fundapi.service.AuthenticationService;
import com.mohdiop.m3fundapi.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projects")
public class CommentController {

    private final CommentService commentService;
    private final AuthenticationService authenticationService;

    public CommentController(CommentService commentService, AuthenticationService authenticationService) {
        this.commentService = commentService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/{projectId}/comments")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentResponse> makeComment(
            @Valid @RequestBody CreateCommentRequest createCommentRequest,
            @PathVariable Long projectId
    ) {
        return ResponseEntity.ok(
                commentService.createComment(
                        authenticationService.getCurrentUserId(),
                        projectId,
                        createCommentRequest
                )
        );
    }

    @PostMapping("/comments/{parentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentResponse> respondComment(
            @Valid @RequestBody CreateCommentRequest createCommentRequest,
            @PathVariable Long parentId
    ) {
        return ResponseEntity.ok(
                commentService.respondToComment(
                        authenticationService.getCurrentUserId(),
                        parentId,
                        createCommentRequest
                )
        );
    }
}
