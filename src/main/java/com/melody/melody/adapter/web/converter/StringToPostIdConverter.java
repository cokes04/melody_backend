package com.melody.melody.adapter.web.converter;


import com.melody.melody.domain.model.Post;
import org.springframework.core.convert.converter.Converter;

public class StringToPostIdConverter implements Converter<String, Post.PostId> {

  @Override
  public Post.PostId convert(String source) {
    return new Post.PostId(Integer.parseInt(source));
  }
}