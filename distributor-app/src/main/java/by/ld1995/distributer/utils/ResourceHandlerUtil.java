package by.ld1995.distributer.utils;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class ResourceHandlerUtil {

    private final static int ONE_MEGA_BYTE = 1024 * 1024;

    public final static BiFunction<FileSystemResource, HttpHeaders, Mono<ResourceRegion>> BUILD_RESOURCE_REGION = (resource, headers) -> {
        long contentLength = 0;
        try {
            contentLength = resource.contentLength();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<HttpRange> httpRanges = headers.getRange();
        if (!httpRanges.isEmpty()) {
            long start = httpRanges.get(0).getRangeStart(contentLength);
            long end = httpRanges.get(0).getRangeEnd(contentLength);
            long rangeLength = Math.min(ONE_MEGA_BYTE, end - start + 1);
            return Mono.just(new ResourceRegion(resource, start, rangeLength));
        } else {
            long rangeLength = Math.min(ONE_MEGA_BYTE, contentLength);
            return Mono.just(new ResourceRegion(resource, 0, rangeLength));
        }
    };

    public final static BiFunction<FileSystemResource, Mono<ResourceRegion>, Mono<ServerResponse>> BUILD_SUCCESS_SERVER_RESPONSE = (video, resourceRegion) ->
            ServerResponse.status(HttpStatus.PARTIAL_CONTENT)
                    .contentType(MediaTypeFactory.getMediaType(video).orElse(MediaType.APPLICATION_OCTET_STREAM))
                    .body(resourceRegion, ResourceRegion.class);

    public final static Function<Throwable, Mono<ServerResponse>> BUILD_ERROR_SERVER_RESPONSE = e ->
            Mono.just("Error " + e.getMessage()).flatMap(message -> ServerResponse.badRequest().bodyValue(message));

    public final static Mono<ServerResponse> RESPONSE_IS_EMPTY = Mono.just("The requested object was not found!").flatMap(message -> ServerResponse.badRequest().bodyValue(message));
}
