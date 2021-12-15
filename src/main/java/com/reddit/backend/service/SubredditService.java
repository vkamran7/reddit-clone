package com.reddit.backend.service;

import com.reddit.backend.dto.SubredditDTO;
import com.reddit.backend.exception.SubredditNotFoundException;
import com.reddit.backend.model.Subreddit;
import com.reddit.backend.repository.SubredditRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
public class SubredditService {

    private final SubredditRepository subredditRepository;
    private final AuthService authService;

    private SubredditDTO mapToDto(Subreddit subreddit) {
        return SubredditDTO.builder()
                .id(subreddit.getId())
                .name(subreddit.getName())
                .description(subreddit.getDescription())
                .postCount(subreddit.getPosts().size())
                .build();
    }

    private Subreddit mapToSubreddit(SubredditDTO subredditDTO) {
        return Subreddit.builder()
                .name("/r/" + subredditDTO.getName())
                .description(subredditDTO.getDescription())
                .user(authService.getCurrentUser())
                .creationDate(Instant.now())
                .build();
    }

    @Transactional(readOnly = true)
    public Page<SubredditDTO> getAll(Integer page) {
        return subredditRepository.findAll(PageRequest.of(page, 100)).map(this::mapToDto);
    }

    @Transactional
    public SubredditDTO save(SubredditDTO subredditDTO) {
        Subreddit subreddit = subredditRepository.save(mapToSubreddit(subredditDTO));
        subredditDTO.setId(subreddit.getId());
        return subredditDTO;
    }

    @Transactional(readOnly = true)
    public SubredditDTO getSubreddit(Long id) {
        Subreddit subreddit = subredditRepository.findById(id)
                .orElseThrow(() -> new SubredditNotFoundException("Subreddit not found with id: " + id));
        return mapToDto(subreddit);
    }
}
