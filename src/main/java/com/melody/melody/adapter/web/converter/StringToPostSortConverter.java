package com.melody.melody.adapter.web.converter;


import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.melody.melody.application.dto.MusicPublish;
import com.melody.melody.application.dto.PostSort;
import com.melody.melody.domain.model.Emotion;
import com.melody.melody.domain.model.Music;
import com.melody.melody.domain.model.User;

public class StringToPostSortConverter implements Converter<String, PostSort> {

  @Override
  public PostSort convert(String source) {
    switch (source){
      case "newest" : return PostSort.newest;
      case "oldest" : return PostSort.oldest;
      default: return PostSort.newest;
    }
  }

  @Override
  public JavaType getInputType(TypeFactory typeFactory)  {
    return typeFactory.constructType(String.class);
  }

  @Override
  public JavaType getOutputType(TypeFactory typeFactory)  {
    return typeFactory.constructType(PostSort.class);
  }
}