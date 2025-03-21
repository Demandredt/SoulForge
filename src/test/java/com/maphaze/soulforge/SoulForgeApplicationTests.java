package com.maphaze.soulforge;

import com.maphaze.soulforge.filesync.mapper.UploadPartMapper;
import com.maphaze.soulforge.filesync.service.FileService;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootTest
class SoulForgeApplicationTests {
    @Autowired
    UploadPartMapper uploadPartMapper;

    @Autowired
    FileService fileService;
    @Autowired
    MinioClient minioClient;
    @Test
    void contextLoads() {
        Iterable<Result<Item>> soulforge = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket("soulforge")
                        .prefix("AdminC:/Users/taosun18/Desktop/wallpaper.png_1")
                        .build()
        );
        soulforge.forEach(itemResult -> {
            try {
                System.out.println(itemResult.get().objectName());
            }catch (Exception e){
                e.printStackTrace();
            }

        });
    }

    @Test
    void testCRC32() throws IOException {
        Path path = Paths.get("C:\\Users\\taosun18\\Downloads\\chunk-1.bin");
        byte[] content = Files.readAllBytes(path);
        MockMultipartFile file = new MockMultipartFile("文件",content);

        System.out.println(fileService.getCRC32(file));
    }

}
