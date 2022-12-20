package com.melody.melody.domain.event;

import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MusicComposed implements Event{
    private long musicId;
    private String musicUrl;
}
