package com.melody.melody.adapter.aws;

import com.melody.melody.domain.exception.type.DomainErrorType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AwsErrorType implements DomainErrorType {
    Failed_Upload_Image_File("400000","이미지 파일 업로드에 실패했습니다."),
    Not_Supported_Media_Type("400001","%s는 지원하지 않는 미디어 타입입니다. 이미지 파일을 업로드 해주세요."),
    Failed_Generate_Music("400002","작곡에 실패하였습니다."),
    Invalid_emotion("400003","감정에 올바른 값이 입력되지 않았습니다."),;

    private final String code;
    private final String messageFormat;
}
