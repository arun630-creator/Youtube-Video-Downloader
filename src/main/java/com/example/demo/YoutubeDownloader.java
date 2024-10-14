package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
public class YoutubeDownloader {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public YoutubeDownloader(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void downloadVideo(String videoUrl, String downloadPath) {
        String command = String.format("yt-dlp -o \"%s\\%%(title)s.%%(ext)s\" -f \"bestvideo+bestaudio\" %s",
                downloadPath, videoUrl);

        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                // Check for download progress
                if (line.contains("[download]")) {
                    String[] parts = line.split(" ");
                    for (String part : parts) {
                        if (part.endsWith("%")) {
                            String progress = part.replace("%", "").trim();
                            messagingTemplate.convertAndSend("/topic/progress", progress);
                            break;
                        }
                    }
                }
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Download completed successfully.");
                messagingTemplate.convertAndSend("/topic/progress", "Download completed.");
            } else {
                messagingTemplate.convertAndSend("/topic/progress", "Download failed with exit code: " + exitCode);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
