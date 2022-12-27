package com.melody.melody.adapter.persistence.post;

import com.melody.melody.adapter.persistence.PersistenceTestConfig;
import com.melody.melody.adapter.persistence.user.TestUserEntityGenerator;
import com.melody.melody.adapter.persistence.user.UserEntity;
import com.melody.melody.domain.model.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DataJpaTest
@Import(PersistenceTestConfig.class)
class PostRepositoryImplTest {
    private PostRepositoryImpl repository;
    private PostJpaRepository jpaRepository;
    private PostMapper mapper;

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    @Autowired
    private TestEntityManager em;

    @BeforeEach
    void setUp() {
        jpaRepository = Mockito.mock(PostJpaRepository.class);
        mapper = Mockito.mock(PostMapper.class);
        repository = new PostRepositoryImpl(jpaRepository, jpaQueryFactory, mapper);
    }

    @Test
    void save_ShouldReturnPostWithId() {
        Post post = TestPostDomainGenerator.randomEmptyIdentityPost();

        Identity postId = TestPostDomainGenerator.randomPostId();
        Post expect = TestPostDomainGenerator.insertIdPost(post, postId);

        PostEntity entity = TestPostEntityGenerator.randomPostEntity(true, false);
        when(mapper.toEntity(post))
                .thenReturn(entity);

        when(jpaRepository.save(entity))
                .thenAnswer(a -> a.getArgument(0, PostEntity.class));

        when(mapper.toModel(entity))
                .thenReturn(expect);

        Post actual = repository.save(post);

        assertFalse(actual.getId().isEmpty());
        assertEquals(expect, actual);
    }

    @Test
    void findById_ShouldReturnPost_WhenExistPost() {
        Post expect = TestPostDomainGenerator.randomOpenPost();
        Identity postId = expect.getId();

        PostEntity entity = TestPostEntityGenerator.randomPostEntity(true, false);
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
        Identity postId = TestPostDomainGenerator.randomPostId();

        PostEntity entity = TestPostEntityGenerator.randomPostEntity(true, false);
        entity.setDeleted(true);

        when(jpaRepository.findById(postId.getValue()))
                .thenReturn(Optional.of(entity));

        Optional<Post> actual = repository.findById(postId);

        assertTrue(actual.isEmpty());
    }

    @Test
    void deleteByUserId_ShouldDeletedUsersPost() {
        UserEntity user1 = TestUserEntityGenerator.saveRandomUserEntity(em);
        Map<Long, PostEntity> user1Posts = TestPostEntityGenerator.saveRandomPostEntitys(em, user1,true, false, 3);
        Map<Long, PostEntity> user1DeletedPosts = TestPostEntityGenerator.saveRandomPostEntitys(em, user1,true, true, 2);

        UserEntity user2 = TestUserEntityGenerator.saveRandomUserEntity(em);
        Map<Long, PostEntity> user2Posts = TestPostEntityGenerator.saveRandomPostEntitys(em, user2,true, false, 4);

        em.flush();
        em.clear();

        Identity user1Id = Identity.from(user1.getId());
        repository.deleteByUserId(user1Id);

        assertDeleted(user1Posts, user1Posts.size());
        assertDeleted(user1DeletedPosts, user1DeletedPosts.size());
        assertDeleted(user2Posts, 0);
    }

    private void assertDeleted(Map<Long, PostEntity> posts, int deletedCount){
        assertEquals(
                deletedCount,
                posts.keySet().stream()
                        .map(id -> em.find(PostEntity.class, id))
                        .filter(PostEntity::isDeleted)
                        .count()
        );
    }
}