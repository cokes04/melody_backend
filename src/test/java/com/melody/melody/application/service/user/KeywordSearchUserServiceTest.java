package com.melody.melody.application.service.user;

import com.melody.melody.application.dto.*;
import com.melody.melody.application.port.out.SearchedUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class KeywordSearchUserServiceTest {
    private KeywordSearchUserService service;
    private SearchedUserRepository repository;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(SearchedUserRepository.class);
        service = new KeywordSearchUserService(repository);
    }

    @Test
    void excute_ShouldReturnList() {
        String keyword = "abc";
        PagingInfo<UserSort> userPaging = new PagingInfo<>(2, 10, UserSort.recent_activity);

        List<SearchedUser> searchedUserList = IntStream.range(0, 10)
                .mapToObj(i -> TestSearchedUserServiceGenerator.randomSearchedUser())
                .collect(Collectors.toList());

        when(repository.search(keyword, userPaging))
                .thenReturn(new PagingResult<>(searchedUserList, searchedUserList.size(), 50, 5));

        KeywordSearchUserService.Command command = new KeywordSearchUserService.Command(keyword, userPaging);
        KeywordSearchUserService.Result result = service.execute(command);
        PagingResult<SearchedUser> actual = result.getPagingResult();
        assertEquals(searchedUserList, actual.getList());
        assertEquals(searchedUserList.size(), actual.getCount());
    }
}