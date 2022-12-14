package com.melody.melody.domain.exception.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UserErrorType implements DomainErrorType{
    Email_Already_Used("100000", "이미 사용중인 이메일입니다."),
    Email_Not_Found("100001", "존재하지 않는 이메일입니다."),
    Passwod_Not_Matches("100002", "패스워드가 일치하지 않습니다."),
    User_Not_Found("100002", "존재하지 않는 회원입니다."),
    User_Already_Withdawn_Status("100004", "회원은 이미 탈퇴 상태입니다."),
    Authentication_Failed("100005", "인증에 실패하였습니다."),
    Not_Permission("100006", "접근 권한이 존재하지 않습니다."),
    NickName_Length_Limit_Exceeded("100007", "닉네임 길이 제한을 초과하였습니다."),
    Invalid_Email_Format("100008", "올바르지 않은 이메일 형식입니다.");

    private final String code;
    private final String messageFormat;
}