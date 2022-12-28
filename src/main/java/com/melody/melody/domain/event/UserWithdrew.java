package com.melody.melody.domain.event;

import lombok.Value;

@Value
public class UserWithdrew implements Event{
    private long userId;
}
