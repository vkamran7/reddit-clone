package com.reddit.backend.controller;

import com.reddit.backend.dto.SubredditDTO;
import com.reddit.backend.service.SubredditService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/subreddit")
@AllArgsConstructor
public class SubredditController {

    SubredditService subredditService;

    @GetMapping("/{page}")
    public ResponseEntity<Page<SubredditDTO>> getAllSubreddits(@PathVariable Integer page) {
        return new ResponseEntity<>(subredditService.getAll(page), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubredditDTO> getSubreddit(@PathVariable Long id) {
        return new ResponseEntity<>(subredditService.getSubreddit(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<SubredditDTO> addSubreddit(@RequestBody @Valid SubredditDTO dto) throws Exception {
        try {
            return new ResponseEntity<>(subredditService.save(dto), HttpStatus.CREATED);
        } catch (Exception ex) {
            throw new Exception("Error while creating subreddit with name: " + dto.getName());
        }
    }
}
