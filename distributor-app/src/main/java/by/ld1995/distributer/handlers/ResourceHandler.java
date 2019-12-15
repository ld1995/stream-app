package by.ld1995.distributer.handlers;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public interface ResourceHandler {

    Mono<ServerResponse> getVideo(ServerRequest request);

    Mono<ServerResponse> getVideoInfo(ServerRequest request);

    Mono<ServerResponse> getVideosInfo(ServerRequest request);
}
