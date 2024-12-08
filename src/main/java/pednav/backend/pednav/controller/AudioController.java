package pednav.backend.pednav.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RestController
public class AudioController {

    @GetMapping("/audio/files")
    public List<String> getAudioFiles() {
        File folder = new File("./");
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".pcm") || name.endsWith(".wav"));

        List<String> fileNames = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                fileNames.add(file.getName());
            }
        }
        return fileNames;
    }
}
