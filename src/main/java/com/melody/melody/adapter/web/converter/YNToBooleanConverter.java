package com.melody.melody.adapter.web.converter;


import org.springframework.core.convert.converter.Converter;

public class YNToBooleanConverter implements Converter<String, Boolean> {
  @Override
  public Boolean convert(String source) {
    return "y".equals(source);
  }
}