package by.ld1995.uploader.handlers;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public interface FileHandler {

    Mono<ServerResponse> uploadVideos(ServerRequest request);

    Mono<ServerResponse> uploadSubtitles(ServerRequest request);
}
