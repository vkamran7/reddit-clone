package com.reddit.backend.repository;

import com.reddit.backend.model.Comment;
import com.reddit.backend.model.Post;
import com.reddit.backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface CommentRepository extends PagingAndSortingRepository<Comment, Long> {
    Page<Comment> findByPost(Post post, Pageable pageable);
    List<Comment> findAllByPost(Post post);
    Page<Comment> findAllByUser(User user, Pageable pageable);
}
