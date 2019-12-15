package by.ld1995.database.entities;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Builder(toBuilder = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Document(collection = "video_info")
@ToString
public class VideoInfo {

    @Id
    private String id;

    private User author;

    @Field
    private String name;

    @Field
    private String extension;

    @Field
    private String hash;

    @Field
    private Instant timestamp;

    @Field(targetType = FieldType.ARRAY)
    @Builder.Default
    private List<Subtitle> subtitles = new ArrayList<>();
}
