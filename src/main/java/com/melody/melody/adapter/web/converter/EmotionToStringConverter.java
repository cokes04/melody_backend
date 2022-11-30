package com.melody.melody.adapter.web.converter;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.melody.melody.domain.model.Emotion;

public class EmotionToStringConverter implements
        org.springframework.core.convert.converter.Converter<Emotion, String>,
        com.fasterxml.jackson.databind.util.Converter<Emotion, String>{

    @Override
    public String convert(Emotion source) {
        return source.name().toLowerCase();
    }

    @Override
    public JavaType getInputType(TypeFactory typeFactory) {
        return typeFactory.constructType(Emotion.class);
    }

    @Override
    public JavaType getOutputType(TypeFactory typeFactory) {
        return typeFactory.constructType(String.class);

    }
}
