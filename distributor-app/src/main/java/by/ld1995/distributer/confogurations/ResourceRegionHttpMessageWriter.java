package by.ld1995.distributer.confogurations;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.ResourceRegionEncoder;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.ZeroCopyHttpOutputMessage;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;
import static org.springframework.http.MediaType.asMediaTypes;

@Slf4j
public class ResourceRegionHttpMessageWriter implements HttpMessageWriter<ResourceRegion> {

    private final ResourceRegionEncoder regionEncoder = new ResourceRegionEncoder();
    private final List<MediaType> mediaTypes = asMediaTypes(regionEncoder.getEncodableMimeTypes());
    private final ResolvableType regionType = ResolvableType.forClass(ResourceRegion.class);

    @Override
    public List<MediaType> getWritableMediaTypes() {
        return mediaTypes;
    }

    @Override
    public boolean canWrite(final ResolvableType elementType, final MediaType mediaType) {
        return regionEncoder.canEncode(elementType, mediaType);
    }

    @Override
    public Mono<Void> write(final Publisher<? extends ResourceRegion> inputStream, final ResolvableType elementType,
                            final MediaType mediaType, final ReactiveHttpOutputMessage message, final Map<String, Object> hints) {
        return null;
    }

    @Override
    public Mono<Void> write(final Publisher<? extends ResourceRegion> inputStream, final ResolvableType actualType,
                            final ResolvableType elementType, final MediaType mediaType, final ServerHttpRequest request,
                            final ServerHttpResponse response, final Map<String, Object> hints) {
        HttpHeaders httpHeaders = response.getHeaders();
        httpHeaders.set(HttpHeaders.ACCEPT_RANGES, "bytes");

        return Mono.from(inputStream).flatMap(resourceRegion -> {
            response.setStatusCode(HttpStatus.PARTIAL_CONTENT);
            MediaType resourceMediaType = getResourceMediaType(mediaType, resourceRegion.getResource());
            httpHeaders.setContentType(resourceMediaType);
            long contentLength = 0;
            try {
                contentLength = resourceRegion.getResource().contentLength();
            } catch (IOException e) {
                e.printStackTrace();
            }
            long start = resourceRegion.getPosition();
            long end = Math.min(start + resourceRegion.getCount() - 1, contentLength - 1);
            String range = String.format("bytes %s-%s/%s", start, end, contentLength);
            httpHeaders.add("Content-Range", range);
            httpHeaders.setContentLength(end - start + 1);

            return zeroCory(resourceRegion.getResource(), resourceRegion, response)
                    .orElseGet(() -> getDefaultBody(resourceRegion, response, resourceMediaType));
        });
    }

    private Mono<Void> getDefaultBody(final ResourceRegion resourceRegion, final ServerHttpResponse response,
                                      final MediaType mediaType) {
        Flux<DataBuffer> dataBufferFlux = this.regionEncoder.encode(
                Mono.just(resourceRegion),
                response.bufferFactory(),
                regionType,
                mediaType,
                Collections.emptyMap());
        return response.writeWith(dataBufferFlux);
    }


    private MediaType getResourceMediaType(final MediaType mediaType, final Resource resource) {
        return (Objects.nonNull(mediaType) && mediaType.isConcrete() && mediaType != APPLICATION_OCTET_STREAM) ? mediaType :
                MediaTypeFactory.getMediaType(resource).orElse(APPLICATION_OCTET_STREAM);
    }

    private Optional<Mono<Void>> zeroCory(final Resource resource, final ResourceRegion resourceRegion,
                                          final ReactiveHttpOutputMessage message) {
        if (message instanceof ZeroCopyHttpOutputMessage && resource.isFile()) {
            try {
                File file = resource.getFile();
                long position = resourceRegion.getPosition();
                long count = resourceRegion.getCount();
                return Optional.of(((ZeroCopyHttpOutputMessage) message).writeWith(file, position, count));
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return Optional.empty();
    }
}
