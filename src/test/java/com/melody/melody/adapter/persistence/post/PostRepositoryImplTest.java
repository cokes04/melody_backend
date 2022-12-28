package com.melody.melody.adapter.persistence.post;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

class PostRepositoryImplTest {

    private PostRepositoryImpl repository;
    private PostDao postDao;

    @BeforeEach
    void setUp() {
        postDao = Mockito.mock(PostDao.class);
        repository = new PostRepositoryImpl(postDao);
    }


}