package by.ld1995.distributer.repositories;

import by.ld1995.database.entities.VideoInfo;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface VideoInfoRepository extends ReactiveMongoRepository<VideoInfo, String> {

    VideoInfo findByName(String name);
}
