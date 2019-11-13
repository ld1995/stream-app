package by.ld1995.distributer.routers;

import by.ld1995.distributer.handlers.ResourceHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

@Configuration
public class ResourceRouter {

    @Bean
    public RouterFunction<ServerResponse> route(final ResourceHandler handler) {
        return RouterFunctions
                .route(GET("/video/{id}"), handler::getVideo)
                .andRoute(GET("/videos"), handler::getVideosInfo);
    }
}
