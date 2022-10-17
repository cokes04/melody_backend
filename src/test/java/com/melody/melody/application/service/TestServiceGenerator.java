package com.melody.melody.application.service;

import com.melody.melody.application.service.music.GenerateMusicService;
import net.datafaker.Faker;
import org.springframework.core.io.UrlResource;

import java.net.MalformedURLException;

public class TestServiceGenerator {
    private static final Faker faker = new Faker();

    public static GenerateMusicService.Image randomImage() throws MalformedURLException {
        return new GenerateMusicService.Image(
                faker.number().numberBetween(1024, 8192),
                "png",
                new UrlResource(faker.internet().image())
        );
    }
}
