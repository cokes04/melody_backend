package com.melody.melody.adapter.web.converter;


import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.melody.melody.application.dto.MusicPublish;
import com.melody.melody.domain.model.Post;

public class StringToPostIdConverter implements Converter<String, Post.PostId> {

  @Override
  public Post.PostId convert(String source) {
    return new Post.PostId(Long.parseLong(source));
  }

  @Override
  public JavaType getInputType(TypeFactory typeFactory) {
    return typeFactory.constructType(String.class);
  }

  @Override
  public JavaType getOutputType(TypeFactory typeFactory) {
    return typeFactory.constructType(Post.PostId.class);
  }
}