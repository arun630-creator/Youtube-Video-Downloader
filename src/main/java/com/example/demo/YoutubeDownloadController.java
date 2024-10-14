package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class YoutubeDownloadController {

    private final YoutubeDownloader youtubeDownloader;

    @Autowired
    public YoutubeDownloadController(YoutubeDownloader youtubeDownloader) {
        this.youtubeDownloader = youtubeDownloader;
    }

    @PostMapping("/download")
    public ResponseEntity<String> downloadVideo(@RequestBody DownloadRequest request) {
        String videoUrl = request.getVideoUrl();
        String downloadPath = request.getDownloadPath();

        // Call the downloader utility to download the video
        youtubeDownloader.downloadVideo(videoUrl, downloadPath);
        return ResponseEntity.ok("Download started...");
    }

    public static class DownloadRequest {
        private String videoUrl;
        private String downloadPath;

        public String getVideoUrl() {
            return videoUrl;
        }

        public void setVideoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
        }

        public String getDownloadPath() {
            return downloadPath;
        }

        public void setDownloadPath(String downloadPath) {
            this.downloadPath = downloadPath;
        }
    }
}
