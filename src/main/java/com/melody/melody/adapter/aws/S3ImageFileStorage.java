package com.melody.melody.adapter.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.melody.melody.application.port.out.ImageFileStorage;
import com.melody.melody.application.service.music.GenerateMusicService;
import com.melody.melody.domain.exception.DomainError;
import com.melody.melody.domain.exception.InvalidArgumentException;
import com.melody.melody.domain.model.Music;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Component
public class S3ImageFileStorage implements ImageFileStorage {
    private final AmazonS3 amazonS3;
    private final String bucketName;
    private final List<String> allowMediaType;

    public S3ImageFileStorage(AmazonS3 amazonS3, S3Config s3Config) {
        this.amazonS3 = amazonS3;
        this.bucketName = s3Config.getBucketName();
        this.allowMediaType = s3Config.getAllowMediaType();
    }

    public Music.ImageUrl save(GenerateMusicService.Image image){
        validate(image);

        String fileName = createFileName(image.getMediaType());
        ObjectMetadata objectMetadata = createObjectMetadata(image);

        try (InputStream inputStream = image.getResource().getInputStream()) {

            amazonS3.putObject(
                    new PutObjectRequest(bucketName, fileName, inputStream, objectMetadata)
            );

        } catch (IOException e) {
            e.printStackTrace();
            throw new FailedFileUploadException(
                    DomainError.of(AwsErrorType.Failed_Upload_Image_File)
            );
        }

        return new Music.ImageUrl(
                amazonS3.getUrl(bucketName, fileName).toString()
        );
    }

    private void validate(GenerateMusicService.Image image){
        if (!allowMediaType.contains(image.getMediaType()))
            throw new InvalidArgumentException(
                    DomainError.of(AwsErrorType.Not_Supported_Media_Type, image.getMediaType())
            );
    }

    private String createFileName(String mediaType) {
        return UUID.randomUUID().toString() + "." + mediaType.split("/")[1];
    }

    private ObjectMetadata createObjectMetadata(GenerateMusicService.Image image){
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(image.getMediaType());
        objectMetadata.setContentLength(image.getSize());

        return objectMetadata;
    }
}
