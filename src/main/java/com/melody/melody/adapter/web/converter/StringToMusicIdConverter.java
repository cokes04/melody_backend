package com.melody.melody.adapter.web.converter;


import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.melody.melody.domain.model.Music;

public class StringToMusicIdConverter implements Converter<String, Music.MusicId> {

  @Override
  public Music.MusicId convert(String source) {
    return new Music.MusicId(Long.parseLong(source));
  }

  @Override
  public JavaType getInputType(TypeFactory typeFactory) {
    return typeFactory.constructType(String.class);
  }

  @Override
  public JavaType getOutputType(TypeFactory typeFactory) {
    return typeFactory.constructType(Music.MusicId.class);
  }
}