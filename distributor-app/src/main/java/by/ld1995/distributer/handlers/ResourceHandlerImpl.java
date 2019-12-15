package by.ld1995.distributer.handlers;

import by.ld1995.database.entities.VideoInfo;
import by.ld1995.distributer.repositories.VideoInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static by.ld1995.distributer.utils.ResourceHandlerUtil.BUILD_ERROR_RESPONSE;
import static by.ld1995.distributer.utils.ResourceHandlerUtil.BUILD_ERROR_SERVER_RESPONSE;
import static by.ld1995.distributer.utils.ResourceHandlerUtil.BUILD_RESOURCE_REGION;
import static by.ld1995.distributer.utils.ResourceHandlerUtil.BUILD_SUCCESS_SERVER_RESPONSE;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResourceHandlerImpl implements ResourceHandler {

    private final static String NOT_FOUND_ERROR_MESSAGE = "The requested object was not found!";
    private final static String FILE_NOT_FOUND_ERROR_MESSAGE = "File not found or could not be read!";

    private final VideoInfoRepository videoInfoRepository;

    @Value("${app.videos.location}")
    private String videosLocation;

    @Override
    public Mono<ServerResponse> getVideo(ServerRequest request) {
        return videoInfoRepository.findById(request.pathVariable("id"))
                .flatMap(videoInfo -> {
                    FileSystemResource video = new FileSystemResource(String.format("%s/%s.%s", videosLocation, videoInfo.getName(), videoInfo.getExtension()));
                    if (video.exists() || video.isReadable()) {
                        Mono<ResourceRegion> resourceRegion = BUILD_RESOURCE_REGION.apply(video, request.headers().asHttpHeaders());
                        return BUILD_SUCCESS_SERVER_RESPONSE.apply(video, resourceRegion);
                    }
                    return BUILD_ERROR_RESPONSE.apply(FILE_NOT_FOUND_ERROR_MESSAGE);
                })
                .log()
                .onErrorResume(BUILD_ERROR_SERVER_RESPONSE)
                .switchIfEmpty(BUILD_ERROR_RESPONSE.apply(NOT_FOUND_ERROR_MESSAGE));
    }

    @Override
    public Mono<ServerResponse> getVideoInfo(ServerRequest request) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(videoInfoRepository.findById(request.pathVariable("id")), VideoInfo.class)
                .onErrorResume(BUILD_ERROR_SERVER_RESPONSE)
                .switchIfEmpty(BUILD_ERROR_RESPONSE.apply(NOT_FOUND_ERROR_MESSAGE));
    }

    @Override
    public Mono<ServerResponse> getVideosInfo(ServerRequest request) {
        return ServerResponse
                .ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(videoInfoRepository.findAll(), VideoInfo.class)
                .onErrorResume(BUILD_ERROR_SERVER_RESPONSE)
                .switchIfEmpty(BUILD_ERROR_RESPONSE.apply(NOT_FOUND_ERROR_MESSAGE));
    }
}
