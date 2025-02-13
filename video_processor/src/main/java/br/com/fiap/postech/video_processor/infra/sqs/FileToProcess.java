package br.com.fiap.postech.video_processor.infra.sqs;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FileToProcess {
    private String bucketDownloadName;
    private String fileName;
}
