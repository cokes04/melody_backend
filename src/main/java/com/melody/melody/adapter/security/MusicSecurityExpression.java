package com.melody.melody.adapter.security;

import com.melody.melody.adapter.web.security.UserDetailsImpl;
import com.melody.melody.application.port.out.MusicRepository;
import com.melody.melody.domain.model.Identity;
import com.melody.melody.domain.model.Music;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MusicSecurityExpression extends AbstractSecurityExpression{
    private final MusicRepository repository;

    public boolean isOwner(Identity musicId){
        Optional<Long> musicOwnerId = repository.findById(musicId).map(m -> m.getUserId().getValue());
        if (musicOwnerId.isEmpty()) return true;

        Optional<UserDetailsImpl> optional = getUserPrincipal();
        if (optional.isEmpty()) return false;

        return musicOwnerId.get().equals(optional.get().getUserId());
    }

    public boolean isOwner(long musicId){
        return isOwner(Identity.from(musicId));
    }

    public boolean isOwner(Music music){
        Optional<UserDetailsImpl> optional = getUserPrincipal();
        if (optional.isEmpty()) return false;

        return  music.getUserId().getValue() == optional.get().getUserId();
    }

    public boolean isExist(Identity musicId){
        return repository.findById(musicId).isPresent();
    }

    public boolean isExist(long musicId){
        return isExist(Identity.from(musicId));
    }
}