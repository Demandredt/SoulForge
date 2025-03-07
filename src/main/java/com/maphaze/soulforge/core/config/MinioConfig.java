package com.maphaze.soulforge.core.config;

import io.minio.MinioClient;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Value("${soul-forge.minio.accessKey}")
    String accessKey;

    @Value("${soul-forge.minio.secretKey}")
    String secretKey;

    @Value("${soul-forge.minio.url}")
    String endpoint;

    @Value("${soul-forge.minio.bucket}")
    String bucket;

    @Schema(description = "初始化Minio客户端")
    @Bean
    public MinioClient minioClient(){
        return MinioClient.builder()
                .credentials(accessKey,secretKey)
                .endpoint(endpoint)
                .build();
    }
}
