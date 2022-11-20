package com.melody.melody.adapter.message.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Value;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MusicComposed {
    private long musicId;
    private String musicUrl;
}
