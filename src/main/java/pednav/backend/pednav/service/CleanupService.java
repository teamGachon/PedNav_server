package pednav.backend.pednav.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pednav.backend.pednav.repository.DataRepository;

@Service
@RequiredArgsConstructor
public class CleanupService {

    private final DataRepository repository;

    @Scheduled(fixedRate = 300000)
    @Transactional
    public void deleteOldData() {
        long now = System.currentTimeMillis();
        long threshold = now - (5 * 60 * 1000); // 5분 전 기준
        repository.deleteOldEntries(threshold);
        System.out.println("🧹 5분 이상 지난 데이터 삭제 완료");
    }
}