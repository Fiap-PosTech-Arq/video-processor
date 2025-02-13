package br.com.fiap.postech.video_processor.infra.sqs;

import br.com.fiap.postech.video_processor.domain.ports.FileServicePortOut;
import br.com.fiap.postech.video_processor.domain.usecases.FileProcessorUseCase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
@Slf4j
public class SqsService {
    private final FileServicePortOut fileService;

    private final FileProcessorUseCase fileProcessorUseCase;

    @SqsListener(value = "https://sqs.us-east-1.amazonaws.com/990374739777/video-manager-sqs")
    public void processSQSMessageContent(@Payload String sqsMessage) {
        try {
            // Usando Jackson para deserializar a mensagem SQS
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(sqsMessage);
            JsonNode record = rootNode.get("Records").get(0);

            // Extrair nome do bucket e a chave do objeto
            String bucketName = record.path("s3").path("bucket").path("name").asText();
            String fileName = record.path("s3").path("object").path("key").asText();

            log.info("processando mensagem");
            FileToProcess fileToProcess = new FileToProcess(bucketName, fileName);

            log.info("fazendo download do bucket s3");
            var file = fileService.download(fileToProcess.getBucketDownloadName(), fileToProcess.getFileName());

            log.info("zipando o arquivo");
            var zipFile = fileProcessorUseCase.processVideoAndExtractImages(file);

            log.info("fazendo upload do arquivo zipado");
            fileService.upload(zipFile, fileToProcess.getFileName() + ".zip");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
