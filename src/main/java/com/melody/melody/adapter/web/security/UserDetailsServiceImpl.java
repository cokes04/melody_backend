package com.melody.melody.adapter.web.security;

import com.melody.melody.application.port.out.UserRepository;
import com.melody.melody.domain.exception.type.UserErrorType;
import com.melody.melody.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(User.Email.from(email))
                .filter(u -> !u.isWithdrawn())
                .map(UserDetailsImpl::new)
                .orElseThrow(()-> new UsernameNotFoundException(UserErrorType.Authentication_Failed.getMessageFormat()));
    }

    public UserDetails loadUserById(User.UserId id) throws UsernameNotFoundException {
        return userRepository.findById(id)
                .filter(u -> !u.isWithdrawn())
                .map(UserDetailsImpl::new)
                .orElseThrow(()-> new UsernameNotFoundException(UserErrorType.Authentication_Failed.getMessageFormat()));
    }
}
