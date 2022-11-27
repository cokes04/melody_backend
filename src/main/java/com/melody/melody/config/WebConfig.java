package com.melody.melody.config;

import com.melody.melody.adapter.web.converter.BooleanToYNConverter;
import com.melody.melody.adapter.web.converter.StringToPostIdConverter;
import com.melody.melody.adapter.web.converter.YNToBooleanConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;

@Configuration
public class WebConfig implements WebMvcConfigurer {


    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new BooleanToYNConverter());
        registry.addConverter(new YNToBooleanConverter());
        registry.addConverter(new StringToPostIdConverter());
    }

    @Bean
    public Filter characterEncodingFilter() {
        return new CharacterEncodingFilter("UTF-8", true, true);
    }

}