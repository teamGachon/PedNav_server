package pednav.backend.pednav.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;
import pednav.backend.pednav.domain.entity.Audio;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.LocalDateTime;

@Component
public class AudioWebSocketHandler extends BinaryWebSocketHandler {

    private final AudioService audioService;

    public AudioWebSocketHandler(AudioService audioService) {
        this.audioService = audioService;
    }

    private static final String AUDIO_FILE_DIRECTORY = "audio_storage/"; // 파일 저장 디렉토리
    private static final Logger logger = LoggerFactory.getLogger(AudioWebSocketHandler.class);

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        logger.info("[{}] WebSocket 연결 성공: 세션 ID = {}", LocalDateTime.now(), session.getId());
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws IOException {
        byte[] audioData = message.getPayload().array(); // PCM 데이터 추출
        logger.info("[{}] 오디오 데이터 수신: {} 바이트, 세션 ID = {}",
                LocalDateTime.now(), audioData.length, session.getId());

        // **디렉토리 확인 및 생성**
        File directory = new File(AUDIO_FILE_DIRECTORY);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                logger.info("[{}] 오디오 저장 디렉토리 생성 완료: {}", LocalDateTime.now(), AUDIO_FILE_DIRECTORY);
            } else {
                logger.error("[{}] 오디오 저장 디렉토리 생성 실패: {}", LocalDateTime.now(), AUDIO_FILE_DIRECTORY);
                return;
            }
        }

        // 유니크한 파일 이름 생성
        String fileName = "audio_" + session.getId() + "_" + System.currentTimeMillis() + ".wav";
        String filePath = AUDIO_FILE_DIRECTORY + fileName;

        // WAV 파일로 저장
        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
            writeWavHeader(fileOutputStream, audioData.length, 44100, 1); // WAV 헤더 작성
            fileOutputStream.write(audioData); // PCM 데이터 작성
            logger.info("[{}] 오디오 데이터를 WAV 파일로 저장 완료: {}", LocalDateTime.now(), filePath);
        } catch (IOException e) {
            logger.error("[{}] 오디오 데이터를 WAV 파일로 저장 중 오류 발생: {}", LocalDateTime.now(), e.getMessage());
            throw e;
        }

        // 데이터베이스에 메타데이터 저장
        Audio audio = Audio.builder()
                .fileName(fileName)
                .filePath(filePath)
                .uploadedAt(LocalDateTime.now())
                .format("WAV") // WAV 포맷 설정
                .sampleRate(44100) // 샘플링 레이트
                .channels(1) // 모노 설정
                .build();

        Audio savedAudio = audioService.saveAudio(audio); // AudioService를 사용해 메타데이터 저장

        // **DB에 저장된 결과를 로그로 출력**
        logger.info("[{}] 오디오 메타데이터 DB 저장 완료: ID = {}, 파일 이름 = {}, 파일 경로 = {}, 업로드 시간 = {}",
                LocalDateTime.now(), savedAudio.getId(), savedAudio.getFileName(),
                savedAudio.getFilePath(), savedAudio.getUploadedAt());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        logger.info("[{}] WebSocket 연결 종료: 세션 ID = {}, 상태 = {}", LocalDateTime.now(), session.getId(), status);
    }

    /**
     * WAV 파일 헤더 작성
     * @param outputStream 파일 출력 스트림
     * @param pcmDataLength PCM 데이터 길이
     * @param sampleRate 샘플링 레이트
     * @param channels 채널 수
     */
    private void writeWavHeader(FileOutputStream outputStream, int pcmDataLength, int sampleRate, int channels) throws IOException {
        int byteRate = sampleRate * channels * 2; // 16비트 오디오의 바이트 레이트 (샘플링 레이트 * 채널 수 * 2)
        int blockAlign = channels * 2; // 블록 정렬 (채널 수 * 2)

        ByteBuffer buffer = ByteBuffer.allocate(44); // WAV 헤더는 44바이트
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        // Chunk ID ("RIFF")
        buffer.put("RIFF".getBytes());
        buffer.putInt(36 + pcmDataLength); // Chunk Size
        buffer.put("WAVE".getBytes()); // Format

        // Subchunk1 ID ("fmt ")
        buffer.put("fmt ".getBytes());
        buffer.putInt(16); // Subchunk1 Size (16 for PCM)
        buffer.putShort((short) 1); // Audio Format (1 for PCM)
        buffer.putShort((short) channels); // Num Channels
        buffer.putInt(sampleRate); // Sample Rate
        buffer.putInt(byteRate); // Byte Rate
        buffer.putShort((short) blockAlign); // Block Align
        buffer.putShort((short) 16); // Bits Per Sample (16비트)

        // Subchunk2 ID ("data")
        buffer.put("data".getBytes());
        buffer.putInt(pcmDataLength); // Subchunk2 Size

        outputStream.write(buffer.array());
    }
}
