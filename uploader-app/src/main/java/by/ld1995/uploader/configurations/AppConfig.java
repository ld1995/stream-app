package by.ld1995.uploader.configurations;

import by.ld1995.uploader.handlers.FileHandler;
import by.ld1995.uploader.handlers.FileHandlerImpl;
import by.ld1995.uploader.repositories.VideoInfoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Configuration
public class AppConfig {

    @Value("${app.videos.location}")
    private String videosLocation;

    @Value("#{'${app.extensions.subtitle}'.split(',')}")
    private List<String> subtitleExtensions;

    @Value("#{'${app.extensions.video}'.split(',')}")
    private List<String> videoExtensions;

    @PostConstruct
    public void createDir() {
        Path dirPath = Paths.get(videosLocation);
        boolean exists = Files.exists(dirPath, LinkOption.NOFOLLOW_LINKS);
        if (!exists) {
            try {
                Files.createDirectories(dirPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Bean
    public FileHandler fileHandler(final VideoInfoRepository videoInfoRepository) {
        return new FileHandlerImpl(videoInfoRepository, videosLocation, subtitleExtensions, videoExtensions);
    }
}
