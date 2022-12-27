package com.melody.melody.adapter.cache.post;

import com.melody.melody.domain.event.PostCreated;
import com.melody.melody.domain.event.PostDeleted;
import com.melody.melody.domain.event.PostOpenChanged;
import com.melody.melody.domain.event.UserWithdrew;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PostCacheEventHandler {
    private final UserPostTotalCache userPostTotalCache;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, classes = PostDeleted.class)
    public void handle(PostDeleted event) {
        userPostTotalCache.update(
                event.getUserId(),
                event.isExistingOpen() ? CountInfo.Open : CountInfo.Close,
                CountInfo.Deleted
                );
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, classes = PostOpenChanged.class)
    public void handle(PostOpenChanged event) {
        userPostTotalCache.update(
                event.getUserId(),
                event.isChangedOpen() ? CountInfo.Close: CountInfo.Open,
                event.isChangedOpen() ? CountInfo.Open : CountInfo.Close
        );
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, classes = PostCreated.class)
    public void handle(PostCreated event) {
        userPostTotalCache.increase(
                event.getUserId(),
                event.isOpen() ? CountInfo.Open: CountInfo.Close
        );
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, classes = UserWithdrew.class)
    public void handle(UserWithdrew event) {
        userPostTotalCache.removeUser(event.getUserId());
    }
}