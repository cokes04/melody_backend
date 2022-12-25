package com.melody.melody.adapter.security;

import com.melody.melody.adapter.web.security.UserDetailsImpl;
import com.melody.melody.application.port.out.MusicRepository;
import com.melody.melody.domain.model.Music;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MusicSecurityExpression extends AbstractSecurityExpression{
    private final MusicRepository repository;

    public boolean isOwner(Music.MusicId musicId){
        Optional<Long> musicOwnerId = repository.findById(musicId).map(m -> m.getUserId().getValue());
        if (musicOwnerId.isEmpty()) return true;

        Optional<UserDetailsImpl> optional = getUserPrincipal();
        if (optional.isEmpty()) return false;

        return musicOwnerId.get().equals(optional.get().getUserId());
    }

    public boolean isExist(Music.MusicId musicId){
        return repository.findById(musicId).isPresent();
    }
}