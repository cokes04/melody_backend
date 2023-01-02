package com.melody.melody.adapter.persistence.post.size;

import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.InvalidArgumentException;
import com.melody.melody.domain.exception.type.PostErrorType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
@Getter
public enum SizeInfo {
        Open("O", com.melody.melody.application.dto.Open.OnlyOpen),
        Close("C", com.melody.melody.application.dto.Open.OnlyClose);

        private final String symbol;
        private final com.melody.melody.application.dto.Open open;

        public static Optional<SizeInfo> get(com.melody.melody.application.dto.Open open){
                for (SizeInfo sizeInfo : SizeInfo.values()){
                        if (sizeInfo.open.equals(open))
                                return Optional.of(sizeInfo);
                }

                return Optional.empty();
        }

        public static SizeInfo getOrElseThrow(com.melody.melody.application.dto.Open open){
                return get(open)
                        .orElseThrow( () -> new InvalidArgumentException(DomainError.of(PostErrorType.Invalid_Open)));
        }
}