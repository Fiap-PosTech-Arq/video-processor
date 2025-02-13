package br.com.fiap.postech.video_processor.infra.repositories.bucket;

import br.com.fiap.postech.video_processor.domain.ports.FileServicePortOut;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.nio.file.Files;
import java.nio.file.Paths;


@Slf4j
@Service
public class FileService implements FileServicePortOut {

    private final S3Client s3Client;
    private final String bucketName;

    public FileService(S3Client s3Client,
                       @Value("${aws.s3.bucketName}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    @Override
    public void upload(String zipFilePath, String objectKey) {

        try{
            var request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            s3Client.putObject(request, RequestBody.fromFile(Paths.get(zipFilePath)));

            System.out.println("Arquivo ZIP enviado para o S3 com sucesso!");
        }catch (S3Exception e) {
            System.err.println("Erro ao enviar para o S3: " + e.awsErrorDetails().errorMessage());
        }
    }

    @Override
    public MultipartFile download(String bucketName, String objectKey) {
        try {

            // Solicitação para baixar o arquivo
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            // Caminho para onde o arquivo será salvo localmente
            String localFilePath = "/tmp/" + objectKey;

            // Baixar o arquivo
            GetObjectResponse getObjectResponse = s3Client.getObject(getObjectRequest,
                    Paths.get(localFilePath));

            // Converter o arquivo baixado para um MultipartFile
            byte[] fileBytes = Files.readAllBytes(Paths.get(localFilePath));
            MultipartFile multipartFile = new MockMultipartFile("video", objectKey, "video/mp4", fileBytes);

            return multipartFile;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

