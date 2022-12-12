package com.melody.melody.domain.event;

import com.melody.melody.domain.model.User;
import lombok.Value;

@Value
public class UserWithdrew implements Event{
    private long userId;
}
