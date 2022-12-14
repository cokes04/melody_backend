package com.melody.melody.domain.exception.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MusicErrorType implements DomainErrorType {
    Not_Found_Music("200000","요청하신 음악을 찾을 수 없습니다."),
    Should_Be_Progress_State_For_Complete_Generation("200001", "작곡을 완료하기 위해서 음악은 작곡 진행 상태여야합니다."),
    Music_Already_Deleted("200002", "이미 제거된 음악입니다."),
    Invailid_Music_Sort("200003", "음악 정렬 기준이 올바르지 않습니다.");

    private final String code;
    private final String messageFormat;
}
