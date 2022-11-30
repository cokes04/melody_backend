package com.melody.melody.adapter.web.converter;


import com.melody.melody.domain.model.Post;
import com.melody.melody.domain.model.User;
import org.springframework.core.convert.converter.Converter;

public class StringToUserIdConverter implements Converter<String, User.UserId> {

  @Override
  public User.UserId convert(String source) {
    return new User.UserId(Long.parseLong(source));
  }
}