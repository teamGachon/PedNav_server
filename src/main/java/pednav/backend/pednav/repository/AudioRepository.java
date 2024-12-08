package pednav.backend.pednav.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pednav.backend.pednav.domain.entity.Audio;

public interface AudioRepository extends JpaRepository<Audio, Long> {
    // 필요한 경우 추가적인 쿼리 메서드 정의 가능
}
