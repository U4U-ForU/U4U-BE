package com.ufu.global.config;

import com.ufu.global.storage.LocalStorageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final LocalStorageProperties localStorageProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get(localStorageProperties.uploadDir()).toAbsolutePath().normalize();
        String uploadResourceLocation = uploadPath.toUri().toString();
        if (!uploadResourceLocation.endsWith("/")) {
            uploadResourceLocation += "/";
        }

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadResourceLocation);
    }
}
