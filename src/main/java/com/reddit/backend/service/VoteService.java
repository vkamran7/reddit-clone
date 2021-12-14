package com.reddit.backend.service;

import com.reddit.backend.dto.VoteDTO;
import com.reddit.backend.exception.PostNotFoundException;
import com.reddit.backend.model.Post;
import com.reddit.backend.model.Vote;
import com.reddit.backend.model.VoteType;
import com.reddit.backend.repository.PostRepository;
import com.reddit.backend.repository.VoteRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class VoteService {
    private final VoteRepository voteRepository;
    private final PostRepository postRepository;
    private final AuthService authService;

    private Vote mapToVote(VoteDTO dto, Post post) {
        return Vote.builder()
                .voteType(dto.getVoteType())
                .post(post)
                .user(authService.getCurrentUser())
                .build();
    }

    @Transactional
    public void vote(VoteDTO dto) {
        Post post = postRepository.findById(dto.getId())
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + dto.getId()));
        Optional<Vote> votePostAndUser =
                voteRepository.findTopByPostAndUserOrderByVoteIdDesc(post, authService.getCurrentUser());

        if (votePostAndUser.isPresent() && votePostAndUser.get().getVoteType().equals(dto.getVoteType())) {
            throw new PostNotFoundException("You have already " + dto.getVoteType() + "\'d for this post");
        }

        if (VoteType.UPVOTE.equals(dto.getVoteType())) {
            post.setVoteCount(post.getVoteCount() + 1);
        } else {
            post.setVoteCount(post.getVoteCount() - 1);
        }
        voteRepository.save(mapToVote(dto, post));
        postRepository.save(post);
    }
}
