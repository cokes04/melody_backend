package com.melody.melody.application.service.post;

import com.melody.melody.application.dto.PostDetail;
import com.melody.melody.application.port.out.PostDetailRepository;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.DomainException;
import com.melody.melody.domain.exception.NotFoundException;
import com.melody.melody.domain.exception.type.PostErrorType;
import com.melody.melody.domain.model.Identity;
import com.melody.melody.domain.model.Post;
import com.melody.melody.domain.model.TestPostDomainGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class GetPostServiceTest {
    private GetPostService service;

    private PostDetailRepository repository;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(PostDetailRepository.class);
        service = new GetPostService(repository);
    }

    @Test
    void excute_ShouldReturnPostDetail() {
        PostDetail postDetail = TestPostDetailServiceGenerator.randomPostDetail();
        Identity postId = Identity.from(postDetail.getId());
        GetPostService.Command command = new GetPostService.Command(postId.getValue());

        when(repository.findById(postId))
                .thenReturn(Optional.of(postDetail));

        GetPostService.Result result = service.execute(command);

        assertEquals(postDetail, result.getPostDetail());
    }

    @Test
    void excute_ShouldException_WhenNotExistPost() {
        Identity postId = TestPostDomainGenerator.randomPostId();
        GetPostService.Command command = new GetPostService.Command(postId.getValue());

        when(repository.findById(postId))
                .thenReturn(Optional.empty());

        assertException(
                () -> service.execute(command),
                NotFoundException.class,
                DomainError.of(PostErrorType.Not_Found_Post)
        );
    }

    void assertException(Runnable runnable, Class< ? extends DomainException> exceptionClass, DomainError... domainErrors){
        assertThatThrownBy(runnable::run)
                .isInstanceOf(exceptionClass)
                .hasFieldOrPropertyWithValue("domainErrors", domainErrors);
    }
}