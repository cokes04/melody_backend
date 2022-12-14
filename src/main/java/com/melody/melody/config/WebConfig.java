package com.melody.melody.config;

import com.melody.melody.adapter.web.converter.*;
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

        registry.addConverter(new StringToPostSortConverter());
        registry.addConverter(new StringToMusicSortConverter());
        registry.addConverter(new StringToMusicPublishConverter());

        registry.addConverter(new YNToBooleanConverter());
        registry.addConverter(new EmotionToStringConverter());
    }

    @Bean
    public Filter characterEncodingFilter() {
        return new CharacterEncodingFilter("UTF-8", true, true);
    }

}