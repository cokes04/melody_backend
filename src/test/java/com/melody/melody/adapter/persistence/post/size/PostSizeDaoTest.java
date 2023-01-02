package com.melody.melody.adapter.persistence.post.size;

import com.melody.melody.adapter.persistence.PersistenceTestConfig;
import com.melody.melody.adapter.persistence.post.TestPostEntityGenerator;
import com.melody.melody.adapter.persistence.user.TestUserEntityGenerator;
import com.melody.melody.adapter.persistence.user.UserEntity;
import com.melody.melody.application.dto.Open;
import com.melody.melody.domain.model.Identity;
import com.melody.melody.domain.model.TestUserDomainGenerator;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Import(PersistenceTestConfig.class)
class PostSizeDaoTest {

    private PostSizeDao dao;

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    @Autowired
    private TestEntityManager em;

    @BeforeEach
    void setUp() {
        dao = new PostSizeDao(jpaQueryFactory);
    }

    @Test
    void findSize_ShouldReturnEverythingSiseExcludeDeleted_WhenHaveDeletedPost() {
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, true,false, 20, 10);
        TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, false,false, 10, 10);

        TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, true,true, 5, 10);
        TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, false,true, 4, 10);

        em.flush();
        em.clear();

        long actual = dao.findSize(Identity.from(userEntity.getId()), Open.Everything);
        assertEquals(30, actual);
    }

    @Test
    void findSize_ShouldReturnEverythingSise_WhenEverything() {
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, true,false, 20, 10);
        TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, false,false, 10, 10);

        em.flush();
        em.clear();

        long actual = dao.findSize(Identity.from(userEntity.getId()), Open.Everything);
        assertEquals(30, actual);
    }

    @Test
    void findSize_ShouldReturnOpenSise_WhenOnlyOpen() {
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, true,false, 20, 10);
        TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, false,false, 10, 10);

        em.flush();
        em.clear();

        long actual = dao.findSize(Identity.from(userEntity.getId()), Open.OnlyOpen);
        assertEquals(20, actual);
    }

    @Test
    void findSize_ShouldReturnCloseSise_WhenClose() {
        UserEntity userEntity = TestUserEntityGenerator.saveRandomUserEntity(em);
        TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, true,false, 20, 10);
        TestPostEntityGenerator.saveRandomPostEntitys(em, userEntity, false,false, 10, 10);

        em.flush();
        em.clear();

        long actual = dao.findSize(Identity.from(userEntity.getId()), Open.OnlyClose);
        assertEquals(10, actual);
    }

    @Test
    void findSize_ShouldReturnZero_WhenNotExistUser() {
        Identity userId = TestUserDomainGenerator.randomUserId();

        long actual = dao.findSize(userId, Open.OnlyClose);
        assertEquals(0, actual);
    }
}