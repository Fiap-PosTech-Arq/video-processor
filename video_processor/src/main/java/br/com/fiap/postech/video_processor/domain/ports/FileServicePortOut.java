package br.com.fiap.postech.video_processor.domain.ports;

import org.springframework.web.multipart.MultipartFile;

public interface FileServicePortOut {
    void upload(String zipFilePath, String objectKey);

    MultipartFile download(String bucketName, String objectKey);
}
