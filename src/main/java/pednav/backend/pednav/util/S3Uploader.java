package pednav.backend.pednav.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3Uploader {

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.s3.access-key}")
    private String accessKey;

    @Value("${aws.s3.secret-key}")
    private String secretKey;

    public String uploadPcm(byte[] pcmData, String folder) {
        String fileName = folder + "/" + UUID.randomUUID() + ".pcm";

        S3Client s3 = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)
                        ))
                .build();

        s3.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(fileName)
                        .contentType("audio/pcm")
                        .build(),
                software.amazon.awssdk.core.sync.RequestBody.fromBytes(pcmData)
        );

        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + fileName;
    }
}
