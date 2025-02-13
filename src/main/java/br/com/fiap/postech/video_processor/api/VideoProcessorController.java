package br.com.fiap.postech.video_processor.api;

import br.com.fiap.postech.video_processor.domain.usecases.FileProcessorUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/videos")
public class VideoProcessorController {

    @Autowired
    private FileProcessorUseCase fileProcessorUseCase;


}
