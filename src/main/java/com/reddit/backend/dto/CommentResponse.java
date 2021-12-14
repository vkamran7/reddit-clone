package com.reddit.backend.dto;

import com.reddit.backend.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {

    private Long id;
    private String text;
    private Long postId;
    private Instant creationDate;
    private String username;
}
