package swiftescaper.backend.swiftescaper.domain;

import jakarta.persistence.*;
import lombok.*;
import swiftescaper.backend.swiftescaper.domain.common.DateBaseEntity;
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Location extends DateBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long tunnalId;

    @Column(nullable = false)
    private Double lat;

    @Column(nullable = false)
    private Double lng;
}
