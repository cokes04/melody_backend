package com.melody.melody.application.service.post;

import com.melody.melody.application.dto.*;
import com.melody.melody.application.port.out.PostDetailRepository;
import com.melody.melody.domain.model.TestUserDomainGenerator;
import com.melody.melody.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class GetUserPostServiceTest {
    private GetUserPostService service;

    private PostDetailRepository repository;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(PostDetailRepository.class);
        service = new GetUserPostService(repository);
    }

    @Test
    void excute_ShouldReturnList() {
        User.UserId userId = TestUserDomainGenerator.randomUserId();
        Open open = Open.Everything;
        PagingInfo<PostSort> postPaging = new PagingInfo<>(2, 10, PostSort.newest);

        List<PostDetail> postDetailList = IntStream.range(0, 10)
                .mapToObj(i -> TestPostDetailServiceGenerator.randomPostDetail())
                .collect(Collectors.toList());

        when(repository.findByUserId(userId, open, postPaging))
                .thenReturn(new PagingResult<>(postDetailList, postDetailList.size(), 50, 5));

        GetUserPostService.Command command = new GetUserPostService.Command(userId, open, postPaging);
        GetUserPostService.Result result = service.execute(command);
        PagingResult<PostDetail> actual = result.getPagingResult();
        assertEquals(postDetailList, actual.getList());
        assertEquals(postDetailList.size(), actual.getCount());
    }
}