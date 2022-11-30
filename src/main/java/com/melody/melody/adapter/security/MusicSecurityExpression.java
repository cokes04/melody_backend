package com.melody.melody.adapter.security;

import com.melody.melody.adapter.web.security.UserDetailsImpl;
import com.melody.melody.application.port.out.MusicRepository;
import com.melody.melody.domain.model.Music;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MusicSecurityExpression {
    private final MusicRepository repository;

    @Setter
    private Authentication authentication;

    public boolean isOwner(Music.MusicId musicId){
        Optional<Long> musicOwnerId = repository.getById(musicId).map(m -> m.getUserId().getValue());
        if (musicOwnerId.isEmpty()) return true;

        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();
        return musicOwnerId.get().equals(user.getUserId());
    }

    public boolean isExist(Music.MusicId musicId){
        return repository.getById(musicId).isPresent();
    }
}