package com.reddit.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {

    private Long postId;
    private String postTitleId;
    private String url;
    private String description;
    private String subredditName;
}
