package com.melody.melody.adapter.persistence.post;

import com.melody.melody.adapter.persistence.user.TestUserEntityGenerator;
import com.melody.melody.adapter.persistence.user.UserEntity;
import com.melody.melody.domain.model.Post;
import com.melody.melody.domain.model.TestPostDomainGenerator;
import com.melody.melody.domain.model.TestUserDomainGenerator;
import com.melody.melody.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PostMapperTest {
    private PostMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new PostMapper();
    }

    @Test
    void toEntity_ShouldReturnEntity() {
        Post post = TestPostDomainGenerator.randomOpenPost();

        PostEntity actual = mapper.toEntity(post);
        assertEqualsModelAndEntity(post, actual);
    }

    @Test
    void toEntity_ShouldReturnNullIdEntity_WhenEmptyIdentity() {
        Post post = TestPostDomainGenerator.randomEmptyIdentityPost();

        PostEntity actual = mapper.toEntity(post);
        assertEqualsModelAndEntity(post, actual);
    }

    @Test
    void toModel_ShouldReturnModel() {
        PostEntity entity = TestPostEntityGenerator.randomPostEntity(false, true);

        Post actual = mapper.toModel(entity);
        assertEqualsModelAndEntity(actual, entity);
    }

    @Test
    void toModel_entity_ShouldReturnEmptyIdentityModel_WhenNullId() {
        PostEntity entity = TestPostEntityGenerator.randomPostEntity(false, true);
        entity.setId(null);

        Post actual = mapper.toModel(entity);
        assertEqualsModelAndEntity(actual, entity);
    }

    private void assertEqualsModelAndEntity(Post post, PostEntity entity){
        if (post.getId().isEmpty())
            assertNull(entity.getId());
        else
            assertEquals(post.getId().getValue(), entity.getId());

        assertEquals(post.getTitle().getValue(), entity.getTitle());
        assertEquals(post.getContent().getValue(), entity.getContent());
        assertEquals(post.isOpen(), entity.isOpen());
        assertEquals(post.isDeleted(), entity.isDeleted());
        assertEquals(post.getUserId().getValue(), entity.getUserEntity().getId());
        assertEquals(post.getMusicId().getValue(), entity.getMusicEntity().getId());
        assertEquals(post.getLikeCount(), entity.getLikeCount());
    }
}