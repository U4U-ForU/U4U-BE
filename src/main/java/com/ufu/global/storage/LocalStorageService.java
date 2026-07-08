package com.ufu.global.storage;

import com.ufu.global.storage.exception.FileUploadFailedException;
import com.ufu.global.storage.exception.InvalidImageFileException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocalStorageService implements StorageService {
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");
    private final LocalStorageProperties localStorageProperties;

    @Override
    public String store(MultipartFile file) {
        validateImage(file);

        Path uploadPath = Paths.get(localStorageProperties.uploadDir()).toAbsolutePath().normalize();
        String storedFileName = UUID.randomUUID() + "." + getExtension(file);

        try {
            Files.createDirectories(uploadPath);
            Files.copy(file.getInputStream(), uploadPath.resolve(storedFileName), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            throw FileUploadFailedException.EXCEPTION;
        }

        return "/uploads/" + storedFileName;
    }

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw InvalidImageFileException.EXCEPTION;
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw InvalidImageFileException.EXCEPTION;
        }

        if (!ALLOWED_EXTENSIONS.contains(getExtension(file))) {
            throw InvalidImageFileException.EXCEPTION;
        }
    }

    private String getExtension(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "" : file.getOriginalFilename());
        int extensionIndex = fileName.lastIndexOf(".");

        if (extensionIndex < 0 || extensionIndex == fileName.length() - 1) {
            throw InvalidImageFileException.EXCEPTION;
        }

        return fileName.substring(extensionIndex + 1).toLowerCase(Locale.ROOT);
    }
}
