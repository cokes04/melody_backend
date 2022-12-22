package com.melody.melody.adapter.web.converter;


import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class BooleanToYNConverter implements Converter<Boolean, String> {

  @Override
  public String convert(Boolean source) {
    return source ? "y" : (source == false ? "n" : null);
  }

  @Override
  public JavaType getInputType(TypeFactory typeFactory) {
    return typeFactory.constructType(Boolean.class);
  }

  @Override
  public JavaType getOutputType(TypeFactory typeFactory) {
    return typeFactory.constructType(String.class);
  }
}