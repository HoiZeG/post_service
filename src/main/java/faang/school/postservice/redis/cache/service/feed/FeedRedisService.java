package faang.school.postservice.redis.cache.service.feed;

import faang.school.postservice.redis.cache.entity.PostRedisCache;

public interface FeedRedisService {

    void addPostToFeed(PostRedisCache post, long subscriberId);

    void deletePostFromFeed(PostRedisCache post, long subscriberId);
}