package faang.school.postservice.redis.service;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.redis.mapper.CommentCacheToCommentDtoMapper;
import faang.school.postservice.redis.mapper.PostCacheMapper;
import faang.school.postservice.redis.repository.CommentCacheRepository;
import faang.school.postservice.redis.repository.FeedCacheRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisFeedCacheService {
    @Value("{spring.data.redis.feed-cache.size:500}")
    private int feedCacheSize;
    @Value("{spring.data.redis.feed-cache.key-prefix}")
    private String feedCacheKeyPrefix;

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisPostCacheService redisPostCacheService;
    private final PostCacheMapper postCacheMapper;
    private final CommentCacheRepository commentCacheRepository;
    private final CommentCacheToCommentDtoMapper commentCacheToCommentDtoMapper;

    public void addPostToFeeds(Long postId, List<Long> followerIds) {
        followerIds.forEach(followerId -> addPostToFollower(postId, followerId));
    }

    public List<PostDto> getUserFeed(Long postId, Long userId) {
        List<Long> postIdList = null;
        if (postId == null){
            postIdList = ((List<Long>) redisTemplate.opsForHash().get(generateFeedCacheKey(userId), "postIdList"))
                    .stream()
                    .limit(20)
                    .toList();
        } else {
            var totalPostIds = (List<Long>) redisTemplate.opsForHash().get(generateFeedCacheKey(userId), "postIdList");
            var postIdIndex = totalPostIds.indexOf(postId);
            postIdList = totalPostIds.subList(postIdIndex, totalPostIds.size());
        }

        return redisPostCacheService.getPostCacheByIds(postIdList)
                .stream()
                .map(postCache -> postCacheMapper.toDto(postCache, commentCacheRepository, commentCacheToCommentDtoMapper))
                .toList();
    }
    private void addPostToFollower(Long postId, Long followerId) {
        String cacheKey = generateFeedCacheKey(followerId);
        redisTemplate.opsForZSet().add(cacheKey, postId, System.currentTimeMillis());

        Long setSize = redisTemplate.opsForZSet().zCard(cacheKey);
        if (setSize != null && setSize > feedCacheSize) {
            redisTemplate.opsForZSet().removeRange(cacheKey, 0, setSize - feedCacheSize);
        }
    }

    private String generateFeedCacheKey(Long followerId) {
        return feedCacheKeyPrefix + followerId;
    }
}