package pednav.backend.pednav.dto;


public class AudioMetadataDTO {
    private String fileName;    // 파일 이름
    private int sampleRate;     // 샘플링 레이트 (예: 44100)
    private int channels;       // 채널 수 (모노 = 1, 스테레오 = 2)
    private String format;      // 오디오 형식 (예: PCM)
    private int sampleSize;     // 샘플 크기 (예: 16비트)
    private double duration;    // 총 길이 (초 단위)

    // Getter and Setter for fileName
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    // Getter and Setter for sampleRate
    public int getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    // Getter and Setter for channels
    public int getChannels() {
        return channels;
    }

    public void setChannels(int channels) {
        this.channels = channels;
    }

    // Getter and Setter for format
    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    // Getter and Setter for sampleSize
    public int getSampleSize() {
        return sampleSize;
    }

    public void setSampleSize(int sampleSize) {
        this.sampleSize = sampleSize;
    }

    // Getter and Setter for duration
    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
         return "AudioMetadataDTO{" +
                "fileName='" + fileName + '\'' +
                ", sampleRate=" + sampleRate +
                ", channels=" + channels +
                ", format='" + format + '\'' +
                ", sampleSize=" + sampleSize +
                ", duration=" + duration +
                '}';
    }


    }

