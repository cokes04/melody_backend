package com.melody.melody.adapter.aws;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@ToString
@Configuration
public class S3Config {
    @Value("${aws.s3.image.credentials.accessKey}")
    private String accessKey;

    @Value("${aws.s3.image.credentials.secretKey}")
    private String secretKey;

    @Value("${aws.s3.image.region}")
    private String region;

    @Value("${aws.s3.image.bucketName}")
    private String bucketName;

    @Value("${aws.s3.image.allowMediaType}")
    private List<String> allowMediaType;

    private BasicAWSCredentials getAWSCredentials() {
        return new BasicAWSCredentials(accessKey, secretKey);
    }

    @Bean
    public AmazonS3 amazonS3() {
        return AmazonS3ClientBuilder
                .standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(this.getAWSCredentials()))
                .build();
    }
}
