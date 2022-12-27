package com.melody.melody.adapter.security;

import com.melody.melody.adapter.web.security.UserDetailsImpl;
import com.melody.melody.domain.model.Identity;
import com.melody.melody.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserSecurityExpressionTest {

    private UserSecurityExpression expression;

    private long authenticUserId = 5;

    @BeforeEach
    void setUp() {
        expression = new UserSecurityExpression();
        setAuthentication(authenticUserId);
    }

    @Test
    void isMe_ShouldReturnTrue_WhenEqualsUserId() {
        Identity userId = Identity.from(5L);

        assertEquals(authenticUserId, userId.getValue());
        boolean actual = expression.isMe(userId);
        assertTrue(actual);
    }

    @Test
    void isMe_ShouldReturnFalse_WhenNotEqualsUserId() {
        Identity userId = Identity.from(10L);

        assertNotEquals(authenticUserId, userId.getValue());
        boolean actual = expression.isMe(userId);
        assertFalse(actual);
    }

    @Test
    void isMe_ShouldReturnFalse_WhenNullAuthentication() {
        Identity userId = Identity.from(10L);
        expression.setAuthentication(null);

        boolean actual = expression.isMe(userId);
        assertFalse(actual);
    }

    void setAuthentication(long userId){
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                UserDetailsImpl.builder().userId(userId).build(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        expression.setAuthentication(authentication);
    }
}