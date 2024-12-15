package pednav.backend.pednav.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;
import pednav.backend.pednav.domain.entity.Audio;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class AudioWebSocketHandler extends BinaryWebSocketHandler {

    private final AudioService audioService;
    private final RestTemplate restTemplate = new RestTemplate(); // FastAPI 호출용 RestTemplate

    boolean isVehicleDetected;
    double resultValue;

    public AudioWebSocketHandler(AudioService audioService) {
        this.audioService = audioService;
    }

    private static final String AUDIO_FILE_DIRECTORY = "audio_storage/"; // 파일 저장 디렉토리
    private static final String FASTAPI_URL = "http://172.25.82.102:8000/predict"; // FastAPI 엔드포인트
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

        // FastAPI로 파일 경로 전송 및 결과값 받기
        Map<String, Object> fastApiResponse = sendFileToFastAPI(filePath);
        boolean isVehicleDetected = (Boolean) fastApiResponse.getOrDefault("vehicleDetected", false);
        double result = (Double) fastApiResponse.getOrDefault("result", 0.0);

        Audio audio = Audio.builder()
                .fileName(fileName)
                .filePath(filePath)
                .uploadedAt(LocalDateTime.now())
                .format("WAV")
                .sampleRate(44100)
                .channels(1)
                .duration((double) audioData.length / (44100 * 2))
                .vehicleDetected(isVehicleDetected)
                .result(result)
                .expirationAt(isVehicleDetected ? null : LocalDateTime.now().plusDays(7)) // 만료 시간 설정
                .build();

        Audio savedAudio = audioService.saveAudio(audio);

        logger.info("[{}] 오디오 메타데이터 DB 저장 완료: ID = {}, 차량 소리 감지 여부 = {}, 결과 값 = {}, 파일 경로 = {}",
                LocalDateTime.now(), savedAudio.getId(),
                isVehicleDetected ? "감지됨" : "미감지",
                result, savedAudio.getFilePath());

        // **결과를 JSON 형식으로 안드로이드로 전송**
        String responseJson = String.format(
                "{\"vehicleDetected\": %b, \"result\": %.2f}",
                isVehicleDetected, result
        );
        session.sendMessage(new TextMessage(responseJson));
        logger.info("[{}] 결과를 안드로이드로 전송: {}", LocalDateTime.now(), responseJson);
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

    /**
     * FastAPI 서버로 파일 경로를 전송하고 결과값을 받아옴
     * @param filePath WAV 파일 경로
     * @return 차량 소리 감지 여부 (true/false)
     */
    private Map<String, Object> sendFileToFastAPI(String filePath) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "multipart/form-data");

            // 파일 읽기
            File file = new File(filePath);
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new FileSystemResource(file));

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // FastAPI 호출
            ResponseEntity<Map> response = restTemplate.postForEntity(FASTAPI_URL, requestEntity, Map.class);

            logger.info("[{}] FastAPI 응답 상태 코드: {}", LocalDateTime.now(), response.getStatusCode());
            logger.info("[{}] FastAPI 응답 헤더: {}", LocalDateTime.now(), response.getHeaders());
            logger.info("[{}] FastAPI 응답 바디: {}", LocalDateTime.now(), response.getBody());


            return response.getBody() != null ? response.getBody() : Map.of();
        } catch (Exception e) {
            logger.error("[{}] FastAPI 호출 실패: {}", LocalDateTime.now(), e.getMessage());
            return Map.of(); // 실패 시 빈 맵 반환
        }
    }
}


