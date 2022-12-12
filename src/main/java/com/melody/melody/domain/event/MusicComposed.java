package com.melody.melody.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Value;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MusicComposed implements Event{
    private long musicId;
    private String musicUrl;
}
