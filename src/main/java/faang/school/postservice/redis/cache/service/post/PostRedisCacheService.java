package faang.school.postservice.redis.cache.service.post;

import faang.school.postservice.redis.cache.entity.PostRedisCache;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PostRedisCacheService {

    CompletableFuture<PostRedisCache> save(PostRedisCache entity, List<Long> subscriberIds);

    void incrementLikes(long postId);

    void incrementViews(long postId);

    void decrementLikes(long postId);

    void deleteById(long postId, List<Long> subscriberIds);
}