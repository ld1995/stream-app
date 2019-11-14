package by.ld1995.uploader.handlers;

import by.ld1995.database.entities.Subtitle;
import by.ld1995.uploader.repositories.VideoInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static by.ld1995.uploader.utils.FileHandlerUtil.ADD_SUBTITLES;
import static by.ld1995.uploader.utils.FileHandlerUtil.BUILD_ERROR_SERVER_RESPONSE;
import static by.ld1995.uploader.utils.FileHandlerUtil.BUILD_SUBTITLE;
import static by.ld1995.uploader.utils.FileHandlerUtil.BUILD_SUCCESS_SERVER_RESPONSE;
import static by.ld1995.uploader.utils.FileHandlerUtil.BUILD_VIDEO_INFO;
import static by.ld1995.uploader.utils.FileHandlerUtil.GET_CONTENT_OF_FILE;
import static by.ld1995.uploader.utils.FileHandlerUtil.GET_FILE_PART_FORM_REQUEST;

@Component
@Slf4j
@RequiredArgsConstructor
public class FileHandlerImpl implements FileHandler {

    //https://github.com/entzik/reactive-spring-boot-examples/blob/master/src/main/java/com/thekirschners/springbootsamples/reactiveupload/ReactiveUploadResource.java

    private final VideoInfoRepository videoInfoRepository;

    @Value("${app.videos.location}")
    private String videosLocation;

    @Value("#{'${app.extensions.subtitle}'.split(',')}")
    private List<String> subtitleExtensions;

    @Value("#{'${app.extensions.video}'.split(',')}")
    private List<String> videoExtensions;

    @Override
    public Mono<ServerResponse> uploadVideos(final ServerRequest request) {
        Consumer<FilePart> saveVideoToDisk = filePart ->
                filePart.transferTo(Paths.get(String.format("%s/%s", videosLocation, filePart.filename())));
        Consumer<FilePart> createDirIfNotExist = (a) -> {
            final Path dirPath = Paths.get(videosLocation);
            boolean exists = Files.exists(dirPath, LinkOption.NOFOLLOW_LINKS);
            if (!exists) {
                try {
                    Files.createDirectories(dirPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        return GET_FILE_PART_FORM_REQUEST.apply(request, videoExtensions)
                .doOnNext(createDirIfNotExist)
                .doOnNext(saveVideoToDisk)
                .map(BUILD_VIDEO_INFO)
                .doOnNext(videoInfo -> videoInfoRepository.save(videoInfo).subscribe())
                .then(BUILD_SUCCESS_SERVER_RESPONSE)
                .onErrorResume(BUILD_ERROR_SERVER_RESPONSE);
    }

    @Override
    public Mono<ServerResponse> uploadSubtitles(final ServerRequest request) {
        Consumer<List<Subtitle>> saveSubtitleToDB = newSubtitles -> videoInfoRepository.findById(request.pathVariable("id"))
                .map(videoInfo -> ADD_SUBTITLES.apply(videoInfo, newSubtitles))
                .doOnNext(videoInfo -> videoInfoRepository.save(videoInfo).subscribe())
                .subscribe();
        return GET_FILE_PART_FORM_REQUEST.apply(request, subtitleExtensions)
                .map(GET_CONTENT_OF_FILE)
                .flatMap(Flux::cache)
                .map(BUILD_SUBTITLE)
                .collect(Collectors.toList())
                .doOnNext(saveSubtitleToDB)
                .then(BUILD_SUCCESS_SERVER_RESPONSE)
                .onErrorResume(BUILD_ERROR_SERVER_RESPONSE);
    }

}
