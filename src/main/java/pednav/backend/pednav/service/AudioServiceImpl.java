package pednav.backend.pednav.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pednav.backend.pednav.domain.entity.Audio;
import pednav.backend.pednav.repository.AudioRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AudioServiceImpl implements AudioService {

    private final AudioRepository audioRepository;

    @Override
    public Audio saveAudio(Audio audio) {
        audio.setUploadedAt(LocalDateTime.now()); // 업로드 시간 설정
        return audioRepository.save(audio);
    }

    @Override
    public List<Audio> getAllAudioFiles() {
        return audioRepository.findAll();
    }

    @Override
    public Audio getAudioById(Long id) {
        return audioRepository.findById(id).orElseThrow(() -> new RuntimeException("Audio file not found!"));
    }
}
