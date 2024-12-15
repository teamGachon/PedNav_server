package pednav.backend.pednav.scheduler;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AudioDataCleaner {

    private final JobLauncher jobLauncher;
    private final Job deleteExpiredAudioJob;

    public AudioDataCleaner(JobLauncher jobLauncher, Job deleteExpiredAudioJob) {
        this.jobLauncher = jobLauncher;
        this.deleteExpiredAudioJob = deleteExpiredAudioJob;
    }

    @Scheduled(cron = "0 0 0 * * *") // 매일 자정에 실행
    public void cleanExpiredAudioData() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("runTime", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(deleteExpiredAudioJob, jobParameters);
            System.out.println("만료된 일반 데이터 삭제 작업 완료");
        } catch (Exception e) {
            System.err.println("일반 데이터 삭제 작업 중 오류 발생: " + e.getMessage());
        }
    }
}
