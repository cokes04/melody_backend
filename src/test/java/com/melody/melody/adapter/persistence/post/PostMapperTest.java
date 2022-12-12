package com.melody.melody.adapter.persistence.post;

import com.melody.melody.domain.model.Post;
import com.melody.melody.domain.model.TestPostDomainGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PostMapperTest {
    private PostMapper postMapper;

    @BeforeEach
    void setUp() {
        postMapper = new PostMapper();
    }

    @Test
    void toModel_ShouldReturnEntity() {
        Post post = TestPostDomainGenerator.randomOpenPost();

        PostEntity actual = postMapper.toEntity(post);
        assertEquals(post.getId().get().getValue(), actual.getId());
        assertEquals(post.getTitle().getValue(), actual.getTitle());
        assertEquals(post.getContent().getValue(), actual.getContent());
        assertEquals(post.isOpen(), actual.isOpen());
        assertEquals(post.isDeleted(), actual.isDeleted());
        assertEquals(post.getUserId().getValue(), actual.getUserEntity().getId());
        assertEquals(post.getMusicId().getValue(), actual.getMusicEntity().getId());
        assertEquals(post.getLikeCount(), actual.getLikeCount());
    }

    @Test
    void toEntity_ShouldReturnModel() {
        PostEntity entity = TestPostEntityGenerator.randomPostEntity(false, true);

        Post actual = postMapper.toModel(entity);
        assertEquals(entity.getId(), actual.getId().get().getValue());
        assertEquals(entity.getTitle(), actual.getTitle().getValue());
        assertEquals(entity.getContent(), actual.getContent().getValue());
        assertEquals(entity.isOpen(), actual.isOpen());
        assertEquals(entity.isDeleted(), actual.isDeleted());
        assertEquals(entity.getLikeCount(), actual.getLikeCount());
        assertEquals(entity.getUserEntity().getId(), actual.getUserId().getValue());
        assertEquals(entity.getMusicEntity().getId(), actual.getMusicId().getValue());
    }
}