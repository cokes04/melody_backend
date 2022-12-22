package com.melody.melody.adapter.web.converter;


import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;

public interface Converter<I, O> extends
        org.springframework.core.convert.converter.Converter<I, O>,
        com.fasterxml.jackson.databind.util.Converter<I, O>{
}