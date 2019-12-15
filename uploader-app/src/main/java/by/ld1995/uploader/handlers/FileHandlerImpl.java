package by.ld1995.uploader.handlers;

import by.ld1995.database.entities.Subtitle;
import by.ld1995.uploader.repositories.VideoInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

@Slf4j
@RequiredArgsConstructor
public class FileHandlerImpl implements FileHandler {

    private final VideoInfoRepository videoInfoRepository;
    private final String videosLocation;
    private final List<String> subtitleExtensions;
    private final List<String> videoExtensions;

    @Override
    public Mono<ServerResponse> uploadVideos(final ServerRequest request) {
//        Predicate<FilePart> isUnique = (filePart) -> {
//            final Optional<String> hash = GenerateHashUtil.generateHash(filePart);
//            if (hash.isPresent()) {
//                System.out.println(hash);
//                return !videoInfoRepository.existsByHash(hash.get());
//            }
//            return false;
//        };
        Consumer<FilePart> saveVideoToDisk = filePart ->
                filePart.transferTo(Paths.get(String.format("%s/%s", videosLocation, filePart.filename())));
        return GET_FILE_PART_FORM_REQUEST.apply(request, videoExtensions)
//                .filter(isUnique)
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
