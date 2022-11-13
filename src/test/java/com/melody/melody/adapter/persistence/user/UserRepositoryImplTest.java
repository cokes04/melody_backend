package com.melody.melody.adapter.persistence.user;

import com.amazonaws.services.apigateway.model.Op;
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
        String email = TestUserDomainGenerator.randomEmail();
        when(jpaRepository.existsByEmail(eq(email)))
                .thenReturn(false);

        boolean actual = userRepository.existsByEmail(email);
        assertFalse(actual);
    }

    @Test
    void existsByEmail_ShouldReturnTrue_WhenSavedEmail() {
        String email = TestUserDomainGenerator.randomEmail();
        when(jpaRepository.existsByEmail(eq(email)))
                .thenReturn(true);

        boolean actual = userRepository.existsByEmail(email);
        assertTrue(actual);
    }

    @Test
    void findByEmail_ShoudReturnEmpty_WhenUnSavedUserEmail() {
        String email = TestUserDomainGenerator.randomEmail();

        when(jpaRepository.findByEmail(eq(email)))
                .thenReturn(Optional.empty());

        Optional<User> actual = userRepository.findByEmail(email);

        assertTrue(actual.isEmpty());
    }

    @Test
    void findByEmail_ShoudReturnUser_WhenSavedUserEmail() {
        UserEntity userEntity = TestUserEntityGenerator.randomUserEntity();
        User user = TestUserDomainGenerator.randomUser();
        String email = user.getEmail();

        when(jpaRepository.findByEmail(eq(email)))
                .thenReturn(Optional.of(userEntity));

        when(mapper.toModel(eq(userEntity)))
                .thenReturn(user);

        Optional<User> actual = userRepository.findByEmail(email);

        assertTrue(actual.isPresent());
        assertEquals(user, actual.get());
    }

    @Test
    void findById_ShouldReturnUser() {
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
}