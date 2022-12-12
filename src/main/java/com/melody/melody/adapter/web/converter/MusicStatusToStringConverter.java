package com.melody.melody.adapter.web.converter;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.melody.melody.domain.model.Emotion;
import com.melody.melody.domain.model.Music;

public class MusicStatusToStringConverter implements
        org.springframework.core.convert.converter.Converter<Music.Status, String>,
        com.fasterxml.jackson.databind.util.Converter<Music.Status, String>{

    @Override
    public String convert(Music.Status source) {
        return source.name().toLowerCase();
    }

    @Override
    public JavaType getInputType(TypeFactory typeFactory) {
        return typeFactory.constructType(Music.Status.class);
    }

    @Override
    public JavaType getOutputType(TypeFactory typeFactory) {
        return typeFactory.constructType(String.class);

    }
}
