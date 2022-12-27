package com.melody.melody.domain.event;

import lombok.Value;

@Value
public class PostDeleted implements Event{
    private long postId;
    private long userId;
    private boolean existingOpen;
}
