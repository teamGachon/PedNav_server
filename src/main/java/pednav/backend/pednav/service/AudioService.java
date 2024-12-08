package pednav.backend.pednav.service;

import pednav.backend.pednav.domain.entity.Audio;

import java.util.List;

public interface AudioService {
    Audio saveAudio(Audio audio); // 소리 데이터 저장
    List<Audio> getAllAudioFiles(); // 저장된 소리 파일 목록 조회
    Audio getAudioById(Long id); // ID로 소리 파일 조회
}
