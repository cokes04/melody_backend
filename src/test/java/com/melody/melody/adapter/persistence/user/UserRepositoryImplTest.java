package com.melody.melody.adapter.persistence.user;

import com.melody.melody.domain.model.TestUserDomainGenerator;
import com.melody.melody.domain.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryImplTest {

    @InjectMocks
    private UserRepositoryImpl userRepository;

    @Mock private UserJpaRepository jpaRepository;
    @Mock private UserMapper mapper;

    @Test
    void save_ShouldReturnUser() {
        User expected = TestUserDomainGenerator.randomUser();
        UserEntity entity = TestUserEntityGenerator.randomUserEntity();

        when(mapper.toEntity(eq(expected)))
                .thenReturn(entity);

        when(jpaRepository.save(eq(entity)))
                .thenReturn(entity);

        when(mapper.toModel(eq(entity)))
                .thenReturn(expected);

        User actual = userRepository.save(expected);

        assertEquals(expected, actual);

        verify(mapper, times(1)).toEntity(eq(expected));
        verify(jpaRepository, times(1)).save(eq(entity));
        verify(mapper, times(1)).toModel(eq(entity));
    }

    @Test
    void existsByEmail_ShouldReturnFalse_WhenUnsavedEmail() {
        User.Email email = TestUserDomainGenerator.randomEmail();
        when(jpaRepository.existsByEmail(eq(email.getValue())))
                .thenReturn(false);

        boolean actual = userRepository.existsByEmail(email);
        assertFalse(actual);
    }

    @Test
    void existsByEmail_ShouldReturnTrue_WhenSavedEmail() {
        User.Email email = TestUserDomainGenerator.randomEmail();
        when(jpaRepository.existsByEmail(eq(email.getValue())))
                .thenReturn(true);

        boolean actual = userRepository.existsByEmail(email);
        assertTrue(actual);
    }

    @Test
    void findByEmail_ShoudReturnEmpty_WhenUnSavedUserEmail() {
        User.Email email = TestUserDomainGenerator.randomEmail();

        when(jpaRepository.findByEmail(eq(email.getValue())))
                .thenReturn(Optional.empty());

        Optional<User> actual = userRepository.findByEmail(email);

        assertTrue(actual.isEmpty());
    }

    @Test
    void findByEmail_ShoudReturnUser_WhenSavedUserEmail() {
        UserEntity userEntity = TestUserEntityGenerator.randomUserEntity();
        User user = TestUserDomainGenerator.randomUser();
        String email = user.getEmail().getValue();

        when(jpaRepository.findByEmail(eq(email)))
                .thenReturn(Optional.of(userEntity));

        when(mapper.toModel(eq(userEntity)))
                .thenReturn(user);

        Optional<User> actual = userRepository.findByEmail(User.Email.from(email));

        assertTrue(actual.isPresent());
        assertEquals(user, actual.get());
    }

    @Test
    void findByEmail_ShouldReturnEmpty_WhenWithrawnUserEmail() {
        UserEntity entity = TestUserEntityGenerator.randomUserEntity();
        String email = entity.getEmail();
        entity.setWithdrawn(true);

        when(jpaRepository.findByEmail(eq(email)))
                .thenReturn(Optional.of(entity));

        Optional<User> actual = userRepository.findByEmail(User.Email.from(email));

        assertTrue(actual.isEmpty());
    }

    @Test
    void findById_ShouldReturnUser_WhenSavedId() {
        UserEntity entity = TestUserEntityGenerator.randomUserEntity();
        User user = TestUserDomainGenerator.randomUser();
        User.UserId userId = user.getId().get();

        when(jpaRepository.findById(eq(userId.getValue())))
                .thenReturn(Optional.of(entity));

        when(mapper.toModel(eq(entity)))
                .thenReturn(user);

        Optional<User> actual = userRepository.findById(userId);

        assertTrue(actual.isPresent());
        assertEquals(user, actual.get());
    }

    @Test
    void findById_ShouldReturnEmpty_WhenUnSavedId() {
        User.UserId userId = TestUserDomainGenerator.randomUserId();

        when(jpaRepository.findById(eq(userId.getValue())))
                .thenReturn(Optional.empty());

        Optional<User> actual = userRepository.findById(userId);

        assertTrue(actual.isEmpty());
    }

    @Test
    void findById_ShouldReturnEmpty_WhenWithrawnUserId() {
        UserEntity entity = TestUserEntityGenerator.randomUserEntity();
        User user = TestUserDomainGenerator.randomUser();
        User.UserId userId = user.getId().get();
        entity.setWithdrawn(true);

        when(jpaRepository.findById(eq(userId.getValue())))
                .thenReturn(Optional.of(entity));

        Optional<User> actual = userRepository.findById(userId);

        assertTrue(actual.isEmpty());
    }
}