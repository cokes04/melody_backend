package com.melody.melody.domain.event;

import lombok.Value;

@Value
public class PostCreated implements Event {
    private long userId;
    private boolean open;
}
