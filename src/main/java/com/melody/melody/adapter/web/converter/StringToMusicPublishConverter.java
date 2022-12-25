package com.melody.melody.adapter.web.converter;


import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.melody.melody.application.dto.MusicPublish;

public class StringToMusicPublishConverter implements Converter<String, MusicPublish>{

  @Override
  public MusicPublish convert(String source) {
    switch (source){
      case "everything" : return MusicPublish.Everything;
      case "published" : return MusicPublish.Published;
      case "unpublished" : return MusicPublish.Unpublished;
      default: return MusicPublish.Published;
    }
  }

  @Override
  public JavaType getInputType(TypeFactory typeFactory)  {
    return typeFactory.constructType(String.class);
  }

  @Override
  public JavaType getOutputType(TypeFactory typeFactory)  {
    return typeFactory.constructType(MusicPublish.class);
  }
}