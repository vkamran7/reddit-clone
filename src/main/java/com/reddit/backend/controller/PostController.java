package com.reddit.backend.controller;

import com.reddit.backend.dto.PostRequest;
import com.reddit.backend.dto.PostResponse;
import com.reddit.backend.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
@AllArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<Void> addPost(@RequestBody PostRequest postRequest) {
        postService.save(postRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<PostResponse>> getAllPosts(@RequestParam Optional<Integer> page) {
        return new ResponseEntity<>(postService.getAllPost(page.orElse(0)), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long id) {
        return new ResponseEntity<>(postService.findById(id), HttpStatus.OK);
    }

    @GetMapping("/sub/{id}")
    public ResponseEntity<Page<PostResponse>> getPostsBySubreddit(@PathVariable Long id,
                                                                  @RequestParam Optional<Integer> page) {
        return new ResponseEntity<>(postService.getPostsBySubredditId(id, page.orElse(0)), HttpStatus.OK);
    }

    @GetMapping("/user/{name}")
    public ResponseEntity<Page<PostResponse>> getPostsByUsername(@PathVariable("name") String username,
                                                                 @RequestParam Optional<Integer> page) {
        return new ResponseEntity<>(postService.getPostsByUsername(username, page.orElse(0)), HttpStatus.OK);
    }
}
