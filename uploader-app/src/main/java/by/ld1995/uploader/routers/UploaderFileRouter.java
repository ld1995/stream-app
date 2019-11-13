package by.ld1995.uploader.routers;

import by.ld1995.uploader.handlers.FileHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class UploaderFileRouter {

    @Bean
    RouterFunction<ServerResponse> route(final FileHandler handler) {
        return RouterFunctions
                .route(POST("/videos").and(accept(MediaType.MULTIPART_FORM_DATA)), handler::uploadVideos)
                .andRoute(POST("/subtitles").and(accept(MediaType.MULTIPART_FORM_DATA)), handler::uploadSubtitles);
    }
}
