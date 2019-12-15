package by.ld1995.uploader.utils;

import by.ld1995.database.entities.Subtitle;
import by.ld1995.database.entities.VideoInfo;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class FileHandlerUtil {

    private final static int FILE_NAME = 0;
    private final static int EXTENSION = 1;
    private final static String SUCCESS_RESPONSE = "Success";
    private final static String LANGUAGE = "Language:";
    private final static String BODY_KEY_FILES = "files";
    private final static String REGEX_FOR_SPLITTING_FILENAME = "\\.(?=[^.]+$)";

    public final static Function<String, String> GET_LANGUAGE = content -> content.substring(content.indexOf(LANGUAGE) + LANGUAGE.length(), content.indexOf("\n\n")).trim();

    public final static Function<String, String[]> GET_FILENAME_AND_EXTENSION = filename -> filename.split(REGEX_FOR_SPLITTING_FILENAME);

    public final static Mono<ServerResponse> BUILD_SUCCESS_SERVER_RESPONSE = ServerResponse.ok().body(Mono.just(SUCCESS_RESPONSE), String.class);

    public final static Function<Throwable, Mono<ServerResponse>> BUILD_ERROR_SERVER_RESPONSE = e ->
            Mono.just("Error " + e.getMessage()).flatMap(message -> ServerResponse.badRequest().bodyValue(message));

    public final static Function<FilePart, Flux<String>> GET_CONTENT_OF_FILE = filePart -> filePart.content()
            .map(DataBuffer::asInputStream)
            .map(InputStreamReader::new)
            .map(BufferedReader::new)
            .map(BufferedReader::lines)
            .map(stringStream -> stringStream.collect(Collectors.joining("\n")));

    public final static BiFunction<VideoInfo, List<Subtitle>, VideoInfo> ADD_SUBTITLES = (videoInfo, subtitles) -> {
        List<Subtitle> jointSubtitles = Stream.of(videoInfo.getSubtitles(), subtitles).flatMap(Collection::stream).collect(Collectors.toList());
        return videoInfo.toBuilder().subtitles(jointSubtitles).build();
    };

    public final static BiFunction<String, List<String>, Mono<Boolean>> EXTENSION_VALIDATION = (filename, extension) -> Mono.just(
            Optional.ofNullable(filename)
                    .filter(f -> f.contains("."))
                    .map(name -> GET_FILENAME_AND_EXTENSION.apply(name)[EXTENSION])
                    .map(extension::contains)
                    .orElse(false));

    public final static Function<FilePart, VideoInfo> BUILD_VIDEO_INFO = filePart -> {
        String[] nameAndExtension = GET_FILENAME_AND_EXTENSION.apply(filePart.filename());
        // add save hash
        return VideoInfo.builder().name(nameAndExtension[FILE_NAME]).extension(nameAndExtension[EXTENSION]).timestamp(Instant.now()).build();
    };

    public final static Function<String, Subtitle> BUILD_SUBTITLE = content -> {
        String language = content.contains(LANGUAGE) ? GET_LANGUAGE.apply(content) : "";
        return Subtitle.builder().content(content).timestamp(Instant.now()).language(language).build();
    };

    public final static BiFunction<ServerRequest, List<String>, Flux<FilePart>> GET_FILE_PART_FORM_REQUEST = (req, extensions) ->
            req.multipartData()
                    .map(it -> it.get(BODY_KEY_FILES))
                    .filter(list -> !CollectionUtils.isEmpty(list))
                    .flatMapMany(Flux::fromIterable)
                    .log()
                    .cast(FilePart.class)
                    .filterWhen(it -> EXTENSION_VALIDATION.apply(it.filename(), extensions))
                    .subscribeOn(Schedulers.parallel());
}
