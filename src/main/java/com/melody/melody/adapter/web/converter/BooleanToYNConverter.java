package com.melody.melody.adapter.web.converter;


import org.springframework.core.convert.converter.Converter;

public class BooleanToYNConverter implements Converter<Boolean, String> {

  @Override
  public String convert(Boolean source) {
    return source ? "y" : "n";
  }
}