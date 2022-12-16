package com.melody.melody.domain.model;

import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.DomainException;
import com.melody.melody.domain.exception.InvalidStatusException;
import com.melody.melody.domain.exception.type.PostErrorType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class PostTest {

    @Test
    void create_ShouldReturnCreatedPost() {
        User.UserId userId = TestUserDomainGenerator.randomUserId();
        Music.MusicId musicId = TestMusicDomainGenerator.randomMusicId();
        Post.Title title = TestPostDomainGenerator.randomTitle();
        Post.Content content = TestPostDomainGenerator.randomContent();
        boolean open = true;

        Post post = Post.create(userId, musicId, title.getValue(), content.getValue(), open);

        assertEquals(userId, post.getUserId());
        assertEquals(musicId, post.getMusicId());
        assertEquals(title, post.getTitle());
        assertEquals(content, post.getContent());
        assertEquals(open, post.isOpen());
        assertFalse(post.isDeleted());
    }

    @Test
    void update_ShouldChangeTitleAndContent() {
        Post post = TestPostDomainGenerator.randomOpenPost();
        Post.Title title = TestPostDomainGenerator.randomTitle();
        Post.Content content = TestPostDomainGenerator.randomContent();

        post.update(title.getValue(), content.getValue());

        assertEquals(title, post.getTitle());
        assertEquals(content, post.getContent());
    }

    @Test
    void like_ShouldPlusLikeCount() {
        Post post = TestPostDomainGenerator.randomOpenPost();
        int expect = post.getLikeCount() + 1;

        post.like();
        assertEquals(expect, post.getLikeCount());
    }

    @Test
    void notLike_ShouldMinusLikeCount() {
        Post post = TestPostDomainGenerator.randomOpenPost();
        int expect = post.getLikeCount() - 1;

        post.notLike();
        assertEquals(expect, post.getLikeCount());
    }

    @Test
    void delete_ShouldDeleted_WhenNotDeletedPost() {
        Post post = TestPostDomainGenerator.randomOpenPost();

        assertFalse(post.isDeleted());
        post.delete();
        assertTrue(post.isDeleted());
    }

    @Test
    void delete_ShouldException_WhenDeletedPost() {
        Post post = TestPostDomainGenerator.randomDeletedPost();

        assertTrue(post.isDeleted());

        assertException(
                post::delete,
                InvalidStatusException.class,
                DomainError.of(PostErrorType.Post_Already_Deleted)
        );
    }

    @Test
    void open_ShouldOpen_WhenOpenPost() {
        Post post = TestPostDomainGenerator.randomOpenPost();

        assertTrue(post.isOpen());
        post.open();
        assertTrue(post.isOpen());
    }

    @Test
    void open_ShouldOpen_WhenClosePost() {
        Post post = TestPostDomainGenerator.randomClosePost();

        assertFalse(post.isOpen());
        post.open();
        assertTrue(post.isOpen());
    }


    @Test
    void close_ShouldClose_WhenOpenPost() {
        Post post = TestPostDomainGenerator.randomOpenPost();

        assertTrue(post.isOpen());
        post.close();
        assertFalse(post.isOpen());
    }

    @Test
    void close_ShouldClose_WhenClosePost() {
        Post post = TestPostDomainGenerator.randomClosePost();

        assertFalse(post.isOpen());
        post.close();
        assertFalse(post.isOpen());
    }

    void assertException(Runnable runnable, Class< ? extends DomainException> exceptionClass, DomainError... domainErrors){
        assertThatThrownBy(runnable::run)
                .isInstanceOf(exceptionClass)
                .hasFieldOrPropertyWithValue("domainErrors", domainErrors);
    }
}