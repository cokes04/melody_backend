package com.melody.melody.adapter.web.converter;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.melody.melody.domain.model.Music;

public class EmotionToStringConverter implements Converter<Music.Emotion, String> {

    @Override
    public String convert(Music.Emotion source) {
        return source.name().toLowerCase();
    }

    @Override
    public JavaType getInputType(TypeFactory typeFactory) {
        return typeFactory.constructType(Music.Emotion.class);
    }

    @Override
    public JavaType getOutputType(TypeFactory typeFactory) {
        return typeFactory.constructType(String.class);

    }
}
