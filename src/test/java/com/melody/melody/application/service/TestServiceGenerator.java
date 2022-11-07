package com.melody.melody.application.service;

import com.melody.melody.application.service.music.GenerateMusicService;
import net.datafaker.Faker;
import org.springframework.core.io.UrlResource;

import java.net.MalformedURLException;

import static com.melody.melody.domain.model.TestMusicDomainGenerator.randomMusic;

public class TestServiceGenerator {
    private static final Faker faker = new Faker();

    public static GenerateMusicService.Image randomImage() throws MalformedURLException {
        return new GenerateMusicService.Image(
                faker.number().numberBetween(1024, 8192),
                "png",
                new UrlResource(faker.internet().image())
        );
    }

    public static GenerateMusicService.Command randomGenerateMusicCommand() throws MalformedURLException {
        return new GenerateMusicService.Command(randomImage(), randomMusicLength(), randomNoise());
    }

    public static GenerateMusicService.Result randomGenerateMusicResult(){
        return new GenerateMusicService.Result(randomMusic());
    }

    public static int randomMusicLength() {
        return randomNumber(60, 100);
    }

    public static int randomNoise(){
        return randomNumber(0, 10);
    }

    private static int randomNumber(int min, int max){
        return faker.number().numberBetween(min, max);
    }
}
