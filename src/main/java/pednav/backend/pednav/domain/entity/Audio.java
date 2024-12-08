package pednav.backend.pednav.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audio_files")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Audio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary Key

    @Column(nullable = false)
    private String fileName; // 파일 이름

    @Column(nullable = false)
    private String filePath; // 파일 경로 (로컬 또는 클라우드)

    @Column(nullable = false)
    private LocalDateTime uploadedAt; // 업로드 시간

    @Column(nullable = true)
    private Integer sampleRate; // 샘플링 레이트 (예: 44100)

    @Column(nullable = true)
    private Integer channels; // 채널 수 (모노 = 1, 스테레오 = 2)

    @Column(nullable = true)
    private String format; // 오디오 형식 (예: PCM, WAV)

    @Column(nullable = true)
    private Double duration; // 소리 데이터의 길이(초 단위)
}
