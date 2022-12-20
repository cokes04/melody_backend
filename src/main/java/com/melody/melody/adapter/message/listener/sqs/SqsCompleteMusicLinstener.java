package com.melody.melody.adapter.message.listener.sqs;

import com.melody.melody.adapter.message.listener.MessageLinstener;
import com.melody.melody.application.handler.MusicComposedEventHandler;
import com.melody.melody.domain.event.MusicComposed;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.cloud.aws.messaging.listener.Acknowledgment;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@RequiredArgsConstructor
@Component
public class SqsCompleteMusicLinstener implements SQSLinstener<MusicComposed> {
    private final MusicComposedEventHandler handler;

    @Override
    @SqsListener(value = "${cloud.aws.sqs.queue.complete-music.uri}", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void receive(@Headers Map<String, String> headers, @Payload MusicComposed musicComposed) {
        handler.handle(musicComposed);
    }
}
