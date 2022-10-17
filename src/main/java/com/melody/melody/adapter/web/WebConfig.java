package com.melody.melody.adapter.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.servlet.Filter;

@Configuration
public class WebConfig {

    @Bean
    public Filter characterEncodingFilter() {
        return new CharacterEncodingFilter("UTF-8", true, true);
    }

}