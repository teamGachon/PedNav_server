package pednav.backend.pednav.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import pednav.backend.pednav.domain.entity.Audio;
import pednav.backend.pednav.repository.AudioRepository;

import jakarta.persistence.EntityManagerFactory; // 변경된 import 경로
import java.io.File;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private final EntityManagerFactory entityManagerFactory;
    private final AudioRepository audioRepository;

    public BatchConfig(EntityManagerFactory entityManagerFactory, AudioRepository audioRepository) {
        this.entityManagerFactory = entityManagerFactory;
        this.audioRepository = audioRepository;
    }

    @Bean
    public JpaPagingItemReader<Audio> audioItemReader() {
        return new JpaPagingItemReaderBuilder<Audio>()
                .name("audioItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT a FROM Audio a WHERE a.vehicleDetected = false")
                .parameterValues(Collections.singletonMap("now", LocalDateTime.now()))
                .pageSize(10)
                .build();
    }

    @Bean
    public CompositeItemWriter<Audio> audioItemWriter() {
        CompositeItemWriter<Audio> writer = new CompositeItemWriter<>();
        writer.setDelegates(List.of(
                items -> {
                    for (Audio item : items) {
                        File file = new File(item.getFilePath());
                        if (file.exists()) {
                            boolean deleted = file.delete();
                            if (deleted) {
                                System.out.println("파일 삭제 완료: " + item.getFilePath());
                            } else {
                                System.err.println("파일 삭제 실패: " + item.getFilePath());
                            }
                        }
                    }
                },
                items -> audioRepository.deleteAll(items) // DB에서 삭제
        ));
        return writer;
    }


    @Bean
    public Step deleteExpiredAudioStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("deleteExpiredAudioStep", jobRepository)
                .<Audio, Audio>chunk(10, transactionManager)
                .reader(audioItemReader())
                .writer(audioItemWriter())
                .build();
    }

    @Bean
    public Job deleteExpiredAudioJob(JobRepository jobRepository, Step deleteExpiredAudioStep) {
        return new JobBuilder("deleteExpiredAudioJob", jobRepository)
                .start(deleteExpiredAudioStep)
                .build();
    }
}
