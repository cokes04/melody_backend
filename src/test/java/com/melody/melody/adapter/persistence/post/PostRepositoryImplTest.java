package com.melody.melody.adapter.persistence.post;

import com.melody.melody.domain.model.Post;
import com.melody.melody.domain.model.TestPostDomainGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class PostRepositoryImplTest {
    private PostRepositoryImpl repository;
    private PostJpaRepository jpaRepository;
    private PostMapper mapper;

    @BeforeEach
    void setUp() {
        jpaRepository = Mockito.mock(PostJpaRepository.class);
        mapper = Mockito.mock(PostMapper.class);
        repository = new PostRepositoryImpl(jpaRepository,mapper);
    }

    @Test
    void save_ShouldReturnPostWithId() {
        Post post = TestPostDomainGenerator.randomNoneIdPost();

        Post.PostId postId = TestPostDomainGenerator.randomPostId();
        Post expect = TestPostDomainGenerator.insertIdPost(post, postId);

        PostEntity entity = TestPostEntityGenerator.randomPostEntity();
        when(mapper.toEntity(post))
                .thenReturn(entity);

        when(jpaRepository.save(entity))
                .thenAnswer(a -> a.getArgument(0, PostEntity.class));

        when(mapper.toModel(entity))
                .thenReturn(expect);

        Post actual = repository.save(post);

        assertTrue(actual.getId().isPresent());
        assertEquals(expect, actual);
    }

    @Test
    void findById_ShouldReturnPost_WhenExistPost() {
        Post expect = TestPostDomainGenerator.randomOpenPost();
        Post.PostId postId = expect.getId().get();

        PostEntity entity = TestPostEntityGenerator.randomPostEntity();
        when(jpaRepository.findById(postId.getValue()))
                .thenReturn(Optional.of(entity));

        when(mapper.toModel(entity))
                .thenReturn(expect);

        Optional<Post> actual = repository.findById(postId);

        assertTrue(actual.isPresent());
        assertEquals(expect, actual.get());
    }

    @Test
    void findById_ShouldReturnEmpty_WhenDeletedPost() {
        Post.PostId postId = TestPostDomainGenerator.randomPostId();

        PostEntity entity = TestPostEntityGenerator.randomPostEntity();
        entity.setDeleted(true);

        when(jpaRepository.findById(postId.getValue()))
                .thenReturn(Optional.of(entity));

        Optional<Post> actual = repository.findById(postId);

        assertTrue(actual.isEmpty());
    }
}