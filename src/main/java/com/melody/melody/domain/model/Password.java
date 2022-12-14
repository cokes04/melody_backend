package com.melody.melody.domain.model;

import lombok.Value;

@Value
public class Password {
    private final String encryptedString;

    public static Password emptyPassword(){
        return new Password("");
    }
}
