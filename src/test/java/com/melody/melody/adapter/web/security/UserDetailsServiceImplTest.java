package com.melody.melody.adapter.web.security;

import com.melody.melody.application.port.out.UserRepository;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.DomainException;
import com.melody.melody.domain.exception.FailedAuthenticationException;
import com.melody.melody.domain.exception.type.UserErrorType;
import com.melody.melody.domain.model.TestUserDomainGenerator;
import com.melody.melody.domain.model.User;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class UserDetailsServiceImplTest {

    private UserDetailsServiceImpl userDetailsServiceImpl;

    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        userDetailsServiceImpl = new UserDetailsServiceImpl(userRepository);
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails() {
        User user = TestUserDomainGenerator.randomUser();
        String email = user.getEmail().getValue();

        when(userRepository.findByEmail(User.Email.from(email)))
                .thenReturn(Optional.of(user));

        UserDetails actual = userDetailsServiceImpl.loadUserByUsername(email);

        assertEquals(email, actual.getUsername());
        assertEquals(user.getPassword().getEncryptedString(), actual.getPassword());
        assertEquals(List.of(new SimpleGrantedAuthority("ROLE_USER")), actual.getAuthorities());
        assertTrue(actual.isAccountNonExpired());
        assertTrue(actual.isAccountNonLocked());
        assertTrue(actual.isCredentialsNonExpired());
        assertTrue(actual.isEnabled());
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenNotFoundEmail() {
        String email = TestUserDomainGenerator.randomEmail().getValue();

        when(userRepository.findByEmail(User.Email.from(email)))
                .thenReturn(Optional.empty());

        assertThatThrownBy( () -> userDetailsServiceImpl.loadUserByUsername(email))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage(UserErrorType.Authentication_Failed.getMessageFormat());
    }

    @Test
    void loadUserById_ShouldReturnUserDetails() {
        User user = TestUserDomainGenerator.randomUser();
        User.UserId id = user.getId().get();

        when(userRepository.findById(id))
                .thenReturn(Optional.of(user));

        UserDetails actual = userDetailsServiceImpl.loadUserById(id);

        assertEquals(user.getEmail().getValue(), actual.getUsername());
        assertEquals(user.getPassword().getEncryptedString(), actual.getPassword());
        assertEquals(List.of(new SimpleGrantedAuthority("ROLE_USER")), actual.getAuthorities());
        assertTrue(actual.isAccountNonExpired());
        assertTrue(actual.isAccountNonLocked());
        assertTrue(actual.isCredentialsNonExpired());
        assertTrue(actual.isEnabled());
    }

    @Test
    void loadUserById_ShouldThrowException_WhenNotFoundId() {
        User.UserId id = TestUserDomainGenerator.randomUserId();

        when(userRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThatThrownBy( () -> userDetailsServiceImpl.loadUserById(id))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage(UserErrorType.Authentication_Failed.getMessageFormat());

    }
}