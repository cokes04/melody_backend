package com.melody.melody.adapter.message.listener.sqs;

import com.melody.melody.adapter.message.listener.MessageLinstener;
import com.melody.melody.domain.event.Event;

import java.util.Map;

public interface SQSLinstener<T extends Event> extends MessageLinstener<T> {
    void receive(Map<String, String> headers, T payload);
}
