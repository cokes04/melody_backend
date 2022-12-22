package com.melody.melody.adapter.web.converter;


import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.melody.melody.application.dto.MusicPublish;
import com.melody.melody.domain.model.User;

public class StringToUserIdConverter implements Converter<String, User.UserId> {

  @Override
  public User.UserId convert(String source) {
    return new User.UserId(Long.parseLong(source));
  }

  @Override
  public JavaType getInputType(TypeFactory typeFactory) {
    return typeFactory.constructType(String.class);
  }

  @Override
  public JavaType getOutputType(TypeFactory typeFactory) {
    return typeFactory.constructType(User.UserId.class);
  }
}