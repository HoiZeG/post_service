package faang.school.postservice.redis.cache.service.feed;

import faang.school.postservice.redis.cache.entity.FeedRedisCache;
import faang.school.postservice.redis.cache.entity.PostRedisCache;
import faang.school.postservice.redis.cache.repository.FeedRedisRepository;
import faang.school.postservice.redis.cache.service.RedisOperations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.NavigableSet;
import java.util.TreeSet;

@Slf4j
@Service
@RequiredArgsConstructor
@Async("feedCacheTaskExecutor")
public class FeedRedisServiceImpl implements FeedRedisService {

    @Value("${spring.data.redis.cache.settings.max-feed-size}")
    private long maxFeedSize;
    private final FeedRedisRepository feedRedisRepository;
    private final RedisOperations redisOperations;

    @Override
    public void addPostToFeed(PostRedisCache post, long subscriberId) {

        FeedRedisCache foundNewsFeed = redisOperations.findById(feedRedisRepository, subscriberId).orElse(null);

        if (foundNewsFeed == null) {

            NavigableSet<PostRedisCache> posts = new TreeSet<>();
            posts.add(post);

            foundNewsFeed = FeedRedisCache.builder()
                    .id(subscriberId)
                    .posts(posts)
                    .build();
        } else {

            NavigableSet<PostRedisCache> currentFeed = foundNewsFeed.getPosts();
            currentFeed.add(post);
            while (currentFeed.size() > maxFeedSize) {
                currentFeed.pollLast();
            }
        }

        redisOperations.updateOrSave(feedRedisRepository, foundNewsFeed, subscriberId);
    }

    @Override
    public void deletePostFromFeed(PostRedisCache post, long subscriberId) {

        FeedRedisCache foundNewsFeed = redisOperations.findById(feedRedisRepository, subscriberId).orElse(null);

        if (foundNewsFeed != null) {

            NavigableSet<PostRedisCache> currentFeed = foundNewsFeed.getPosts();
            currentFeed.remove(post);
            redisOperations.updateOrSave(feedRedisRepository, foundNewsFeed, subscriberId);
        }
    }
}
