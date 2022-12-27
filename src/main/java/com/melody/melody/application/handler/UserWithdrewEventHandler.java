package com.melody.melody.application.handler;

import com.melody.melody.application.port.in.EventHandler;
import com.melody.melody.application.port.out.MusicRepository;
import com.melody.melody.application.port.out.PostRepository;
import com.melody.melody.domain.event.UserWithdrew;
import com.melody.melody.domain.model.Identity;
import com.melody.melody.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class UserWithdrewEventHandler implements EventHandler<UserWithdrew> {
    private final PostRepository postRepository;
    private final MusicRepository musicRepository;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, classes = UserWithdrew.class)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void handle(UserWithdrew event) {
        Identity userId = Identity.from(event.getUserId());
        postRepository.deleteByUserId(userId);
        musicRepository.deleteByUserId(userId);
    }
}
