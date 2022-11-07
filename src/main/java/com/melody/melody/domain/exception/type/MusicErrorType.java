package com.melody.melody.domain.exception.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MusicErrorType implements DomainErrorType {
    Not_Found_Music("0","요청하신 음악을 찾을 수 없습니다."),
    Should_Be_Progress_State_For_Complete_Generation("0", "작곡을 완료하기 위해서 음악은 작곡 진행 상태여야합니다."),;

    private final String code;
    private final String messageFormat;
}
