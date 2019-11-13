package by.ld1995.distributer.handlers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;

//@Service
public class VideoService {

    private final static int ONE_MEGA_BYTE = 1024 * 1024;

//    @Value("${app.videos.location}")
////    private String videosLocation;
////
////    @Value("#{'${app.extensions.video}'.split(',')}")
////    private List<String> videoFormats;

//    public UrlResource createResource(final String videoName) throws MalformedURLException {
//        return new UrlResource(String.format("file:%s/%s.%s", videosLocation, videoName, videoFormats.get(0)));
//    }

//    public ResourceRegion getPartOfVideo(final UrlResource videoResource, final HttpHeaders headers) throws IOException {
//        long contentLength = videoResource.contentLength();
//        List<HttpRange> httpRanges = headers.getRange();
//        if (!httpRanges.isEmpty()) {
//            long start = httpRanges.get(0).getRangeStart(contentLength);
//            long end = httpRanges.get(0).getRangeEnd(contentLength);
//            long rangeLength = Math.min(ONE_MEGA_BYTE, end - start + 1);
//            return new ResourceRegion(videoResource, start, rangeLength);
//        } else {
//            long rangeLength = Math.min(ONE_MEGA_BYTE, contentLength);
//            return new ResourceRegion(videoResource, 0, rangeLength);
//        }
//    }
}
