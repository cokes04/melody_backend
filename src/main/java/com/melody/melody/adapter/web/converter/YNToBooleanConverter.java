package com.melody.melody.adapter.web.converter;


import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.netty.util.internal.StringUtil;

public class YNToBooleanConverter implements
        org.springframework.core.convert.converter.Converter<String, Boolean>,
        com.fasterxml.jackson.databind.util.Converter<String, Boolean>{

  @Override
  public Boolean convert(String source) {
    return StringUtil.isNullOrEmpty(source) ? null : "y".equals(source);
  }

  @Override
  public JavaType getInputType(TypeFactory typeFactory) {
    return typeFactory.constructType(String.class);
  }

  @Override
  public JavaType getOutputType(TypeFactory typeFactory) {
    return typeFactory.constructType(Boolean.class);
  }
}