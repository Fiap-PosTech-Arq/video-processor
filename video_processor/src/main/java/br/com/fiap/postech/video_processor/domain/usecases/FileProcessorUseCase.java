package br.com.fiap.postech.video_processor.domain.usecases;

import br.com.fiap.postech.video_processor.domain.ports.FileServicePortOut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Slf4j
@RequiredArgsConstructor
@Service
public class FileProcessorUseCase {
    String tempDir = System.getProperty("java.io.tmpdir") + "/frames/";


    public String processVideoAndExtractImages(MultipartFile videoFile) throws Exception {
        // Crie um diretório temporário para salvar imagens extraídas
        Path tempDirectory = Paths.get(tempDir);
        if (!Files.exists(tempDirectory)) {
            Files.createDirectory(tempDirectory);
        }


        // Salvar o arquivo de vídeo temporariamente
        File video = new File(tempDir + videoFile.getOriginalFilename());
        videoFile.transferTo(video);

        // Extrair frames do vídeo
        extractFramesFromVideo(tempDirectory.toString(), videoFile.getOriginalFilename());


        // Caminho temporário onde o arquivo ZIP será gerado
        String zipFilePath = System.getProperty("java.io.tmpdir") + "/output/";

        try {
            // Gera o arquivo ZIP (compactando a pasta)
            zipDirectory(tempDirectory.toString(), zipFilePath);

            return zipFilePath;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deleteTempFiles(Path tempDirectory) throws IOException {
        Files.walk(tempDirectory)
                .map(Path::toFile)
                .forEach(File::delete);
    }

    public void extractFramesFromVideo (String caminhoVideo, String fileName){
        try{
            String caminhoArquivo = caminhoVideo + "/" + fileName;
            String comando = "ffmpeg -i " + caminhoArquivo + " -vf fps=1 " + caminhoVideo + "/frame_%04d.png";
            System.out.println(comando);

            Process process = Runtime.getRuntime().exec(comando);

            // Lê a saída do comando (para debug)
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);  // Aqui você pode ver a saída do comando no console
            }

            // Lê a saída de erro (se houver)
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = errorReader.readLine()) != null) {
                System.err.println(line);  // Exibe erros, se houver
            }

            // Aguarda o término do processo
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Frames extraídos com sucesso!");
            } else {
                System.err.println("Erro ao executar o comando FFmpeg. Código de saída: " + exitCode);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void zipDirectory(String folderPath, String zipFilePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(zipFilePath);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            Path sourceDir = Paths.get(folderPath);

            if (!Files.exists(sourceDir) || !Files.isDirectory(sourceDir)) {
                throw new IOException("A pasta de origem não existe ou não é um diretório válido.");
            }

            File folder = new File(folderPath);
            String[] files = folder.list();


            Files.walk(sourceDir)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        try {
                            Path relativePath = sourceDir.relativize(path);
                            ZipEntry zipEntry = new ZipEntry(relativePath.toString());
                            zos.putNextEntry(zipEntry);
                            Files.copy(path, zos);
                            zos.closeEntry();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }
    }

}
