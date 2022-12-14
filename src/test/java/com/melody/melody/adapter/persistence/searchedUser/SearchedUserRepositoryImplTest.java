package com.melody.melody.adapter.persistence.searchedUser;

import com.melody.melody.adapter.persistence.PersistenceTestConfig;
import com.melody.melody.adapter.persistence.post.PostEntity;
import com.melody.melody.adapter.persistence.post.TestPostEntityGenerator;
import com.melody.melody.adapter.persistence.postdetail.PostDetailRepositoryImpl;
import com.melody.melody.adapter.persistence.user.TestUserEntityGenerator;
import com.melody.melody.adapter.persistence.user.UserEntity;
import com.melody.melody.application.dto.*;
import com.melody.melody.domain.model.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(PersistenceTestConfig.class)
class SearchedUserRepositoryImplTest {
    private SearchedUserRepositoryImpl repository;

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    @Autowired
    private TestEntityManager em;

    @BeforeEach
    void setUp() {
        repository = new SearchedUserRepositoryImpl(jpaQueryFactory);
    }

    @Test
    void findByUserId_ShuoldReturnIncludedKeywordSarchedUserList() {
        String keyword =  "abc";
        Map<Long, UserEntity> map = TestUserEntityGenerator.saveRandomUserEntitys(em, keyword, null,5, 1);
        Map<Long, UserEntity> notIncludedKeywordUserMap = TestUserEntityGenerator.saveRandomUserEntitys(em, "twejklrqwds", keyword,5, 1);

        List<UserEntity> sortedList = map.values().stream()
                .sorted( (a, b) -> a.getLastActivityDate().isBefore(b.getLastActivityDate()) ? 1 : -1)
                .collect(Collectors.toList());

        em.flush();
        em.clear();

        PagingResult<SearchedUser> actual = repository.search(keyword, new PagingInfo<UserSort>(0, 5, UserSort.recent_activity));
        assertEquals(5, actual.getCount());
        assertEquals(5, actual.getTotalCount());
        assertEquals(1, actual.getTotalPage());
        for (int i = 0; i < 5; i++){
            assertEqualsEntityAndSearchedUser(sortedList.get(i), actual.getList().get(i));
        }
    }

    @Test
    void findByUserId_ShuoldReturnOldestSortedList_WhenRecentActivitySort() {
        String keyword =  "abc";
        Map<Long, UserEntity> map = TestUserEntityGenerator.saveRandomUserEntitys(em, keyword, null,20, 1);

        List<UserEntity> sortedList = map.values().stream()
                .sorted( (a, b) -> a.getLastActivityDate().isBefore(b.getLastActivityDate()) ? 1 : -1)
                .skip(0)
                .limit(8)
                .collect(Collectors.toList());

        em.flush();
        em.clear();

        PagingResult<SearchedUser> actual = repository.search(keyword, new PagingInfo<UserSort>(0, 8, UserSort.recent_activity));
        assertEquals(8, actual.getCount());
        assertEquals(20, actual.getTotalCount());
        assertEquals(3, actual.getTotalPage());
        for (int i = 0; i < 8; i++){
            assertEqualsEntityAndSearchedUser(sortedList.get(i), actual.getList().get(i));
        }
    }

    @Test
    void findByUserId_ShuoldReturnSecondList_WhenSecondPage() {
        String keyword =  "abc";
        Map<Long, UserEntity> map = TestUserEntityGenerator.saveRandomUserEntitys(em, keyword, null, 20, 1);

        List<UserEntity> sortedList = map.values().stream()
                .sorted( (a, b) -> a.getLastActivityDate().isBefore(b.getLastActivityDate()) ? 1 : -1)
                .skip(8)
                .limit(8)
                .collect(Collectors.toList());

        em.flush();
        em.clear();

        PagingResult<SearchedUser> actual = repository.search(keyword, new PagingInfo<UserSort>(1, 8, UserSort.recent_activity));
        assertEquals(8, actual.getCount());
        assertEquals(20, actual.getTotalCount());
        assertEquals(3, actual.getTotalPage());
        for (int i = 0; i < 8; i++){
            assertEqualsEntityAndSearchedUser(sortedList.get(i), actual.getList().get(i));
        }
    }

    @Test
    void findByUserId_ShuoldReturnLastList_WhenLastPage() {
        String keyword =  "abc";
        Map<Long, UserEntity> map = TestUserEntityGenerator.saveRandomUserEntitys(em, keyword, null, 20, 1);

        List<UserEntity> sortedList = map.values().stream()
                .sorted( (a, b) -> a.getLastActivityDate().isBefore(b.getLastActivityDate()) ? 1 : -1)
                .skip(16)
                .limit(8)
                .collect(Collectors.toList());

        em.flush();
        em.clear();

        PagingResult<SearchedUser> actual = repository.search(keyword, new PagingInfo<UserSort>(2, 8, UserSort.recent_activity));
        assertEquals(4, actual.getCount());
        assertEquals(20, actual.getTotalCount());
        assertEquals(3, actual.getTotalPage());
        for (int i = 0; i < 4; i++){
            assertEqualsEntityAndSearchedUser(sortedList.get(i), actual.getList().get(i));
        }
    }

    public void  assertEqualsEntityAndSearchedUser(UserEntity entity, SearchedUser searchedUser){
        assertEquals(entity.getId(), searchedUser.getUserId());
        assertEquals(entity.getNickName(), searchedUser.getNickName());
    }
}