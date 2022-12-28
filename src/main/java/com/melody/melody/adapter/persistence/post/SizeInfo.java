package com.melody.melody.adapter.persistence.post;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SizeInfo {
        Open(false),Close(false), Deleted(true);

        private final boolean deleted;
}