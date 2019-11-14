package by.ld1995.distributer.handlers;

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

import static by.ld1995.distributer.utils.ResourceHandlerUtil.BUILD_ERROR_SERVER_RESPONSE;
import static by.ld1995.distributer.utils.ResourceHandlerUtil.BUILD_RESOURCE_REGION;
import static by.ld1995.distributer.utils.ResourceHandlerUtil.BUILD_SUCCESS_SERVER_RESPONSE;
import static by.ld1995.distributer.utils.ResourceHandlerUtil.RESPONSE_IS_EMPTY;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResourceHandlerImpl implements ResourceHandler {

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
                    return ServerResponse.badRequest().bodyValue("File not found or could not be read!");
                })
                .onErrorResume(BUILD_ERROR_SERVER_RESPONSE)
                .switchIfEmpty(RESPONSE_IS_EMPTY);
    }

    //   todo EventSource https://learn.javascript.ru/server-sent-events
    @Override
    public Mono<ServerResponse> getVideosInfo(ServerRequest request) {
        return ServerResponse
                .ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .bodyValue(videoInfoRepository.findAll())
                .onErrorResume(BUILD_ERROR_SERVER_RESPONSE)
                .switchIfEmpty(RESPONSE_IS_EMPTY);
    }
}
