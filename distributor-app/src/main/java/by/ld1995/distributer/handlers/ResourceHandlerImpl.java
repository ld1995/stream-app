package by.ld1995.distributer.handlers;

import by.ld1995.database.entities.VideoInfo;
import by.ld1995.distributer.repositories.VideoInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResourceHandlerImpl implements ResourceHandler {

    private final static int ONE_MEGA_BYTE = 1024 * 1024;

    private final static BiFunction<UrlResource, HttpHeaders, ResourceRegion> BUILD_RESOURCE_REGION = (urlResource, headers) -> {
        long contentLength = 0;
        try {
            contentLength = urlResource.contentLength();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<HttpRange> httpRanges = headers.getRange();
        if (!httpRanges.isEmpty()) {
            long start = httpRanges.get(0).getRangeStart(contentLength);
            long end = httpRanges.get(0).getRangeEnd(contentLength);
            long rangeLength = Math.min(ONE_MEGA_BYTE, end - start + 1);
            return new ResourceRegion(urlResource, start, rangeLength);
        } else {
            long rangeLength = Math.min(ONE_MEGA_BYTE, contentLength);
            return new ResourceRegion(urlResource, 0, rangeLength);
        }
    };

//    private final static Function<Throwable, Mono<ServerResponse>> BUILD_ERROR_SERVER_RESPONSE = e ->
//            Mono.just("Error " + e.getMessage()).flatMap(message -> ServerResponse.badRequest().bodyValue(message));

    private final static Function<Mono<ResourceRegion>, Mono<ServerResponse>> BUILD_SUCCESS_SERVER_RESPONSE = (resourceRegion) ->
            ServerResponse
                    .status(HttpStatus.PARTIAL_CONTENT)
                    .contentType(MediaTypeFactory.getMediaType("video/mp4").orElse(MediaType.APPLICATION_OCTET_STREAM))
                    .body(resourceRegion, ResourceRegion.class);
    //    private final static String SUBTITLE_MEDIA_TYPE = "text/vvt: charset=utf-8";

    private final VideoInfoRepository videoInfoRepository;

    @Value("${app.videos.location}")
    private String videosLocation;

    @Override
    public Mono<ServerResponse> getVideo(ServerRequest request) {
        Mono<ResourceRegion> resourceRegion = videoInfoRepository.findById(request.pathVariable("id"))
                .map(videoInfo -> {
                    try {
                        return new UrlResource(String.format("file:%s/%s.%s",
                                videosLocation, videoInfo.getName(), videoInfo.getExtension()));
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(urlResource -> BUILD_RESOURCE_REGION.apply(urlResource, request.headers().asHttpHeaders()));
        return BUILD_SUCCESS_SERVER_RESPONSE.apply(resourceRegion);

//        try {
//            UrlResource video = videoService.createResource(videoName);
//            Mono<ResourceRegion> videosLocation = Mono.just(videoService.getPartOfVideo(video, request.headers().asHttpHeaders()));
//            return ServerResponse.status(HttpStatus.PARTIAL_CONTENT)
//                    .contentType(MediaTypeFactory.getMediaType(video).orElse(MediaType.APPLICATION_OCTET_STREAM))
//                    .body(videosLocation, ResourceRegion.class);
//        } catch (IOException e) {
//            return ServerResponse.badRequest().body(BodyInserters.fromObject("An error occurred while receiving video"));
//        }
    }

    //   todo EventSource https://learn.javascript.ru/server-sent-events
    @Override
    public Mono<ServerResponse> getVideosInfo(ServerRequest request) {
        return ServerResponse
                .ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(videoInfoRepository.findAll(), VideoInfo.class);
    }
}
