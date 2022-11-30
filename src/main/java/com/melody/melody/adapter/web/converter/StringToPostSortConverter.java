package com.melody.melody.adapter.web.converter;


import com.melody.melody.application.dto.PostSort;
import com.melody.melody.domain.model.User;
import org.springframework.core.convert.converter.Converter;

public class StringToPostSortConverter implements Converter<String, PostSort> {

  @Override
  public PostSort convert(String source) {
    switch (source){
      case "newest" : return PostSort.newest;
      case "oldest" : return PostSort.oldest;
      default: return PostSort.newest;
    }
  }
}