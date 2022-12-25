package com.melody.melody.adapter.web.converter;


import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.melody.melody.application.dto.MusicSort;

public class StringToMusicSortConverter implements Converter<String, MusicSort>{

  @Override
  public MusicSort convert(String source) {
    switch (source){
      case "newest" : return MusicSort.newest;
      case "oldest" : return MusicSort.oldest;
      default: return MusicSort.newest;
    }
  }

  @Override
  public JavaType getInputType(TypeFactory typeFactory)  {
    return typeFactory.constructType(String.class);
  }

  @Override
  public JavaType getOutputType(TypeFactory typeFactory)  {
    return typeFactory.constructType(MusicSort.class);
  }
}