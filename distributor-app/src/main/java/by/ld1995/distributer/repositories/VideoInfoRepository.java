package by.ld1995.distributer.repositories;

import by.ld1995.database.entities.VideoInfo;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoInfoRepository extends ReactiveMongoRepository<VideoInfo, String> {
}
