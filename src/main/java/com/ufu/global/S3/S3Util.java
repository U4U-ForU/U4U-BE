package com.ufu.global.S3;

import com.ufu.global.S3.exception.FileUploadFailedException;
import com.ufu.global.S3.exception.InvalidImageFileException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class S3Util {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final S3Client s3Client;

    public String upload(MultipartFile file, String directory) {
        validateImage(file);
        String fileName = createFileName();
        String key = directory + "/" + fileName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();

        try {
            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            GetUrlRequest getUrlRequest = GetUrlRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            return s3Client.utilities().getUrl(getUrlRequest).toString();
        } catch (IOException | SdkException exception) {
            log.error("S3 파일 업로드에 실패했습니다. key={}", key, exception);
            throw FileUploadFailedException.EXCEPTION;
        }
    }

    private void validateImage(MultipartFile file) {
        if (file == null
                || file.isEmpty()
                || !Objects.equals(file.getContentType(), "image/png")) {
            throw InvalidImageFileException.EXCEPTION;
        }
    }

    private String createFileName() {
        return UUID.randomUUID() + ".png";
    }
}
