package faang.school.postservice.redis.service;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.redis.mapper.PostCacheMapper;
import faang.school.postservice.redis.model.PostCache;
import faang.school.postservice.redis.repository.PostCacheRedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@Slf4j
public class RedisPostCacheService {
    @Value("{spring.data.redis.post-cache.views}")
    private String postCacheViewsField;
    @Value("{spring.data.redis.post-cache.likes}")
    private String postCacheLikesField;
    @Value("{spring.data.redis.post-cache.comments-in-post:3}")
    private int commentsInPostQuantity;
    @Value("{spring.data.redis.post-cache.key-prefix}")
    private String postCacheKeyPrefix;
    @Qualifier("redisCacheTemplate")
    private final RedisTemplate<String, Object> redisTemplate;
    private final PostCacheRedisRepository postCacheRedisRepository;
    private final PostCacheMapper postCacheMapper;

    public RedisPostCacheService(RedisTemplate<String, Object> redisTemplate, PostCacheRedisRepository postCacheRedisRepository, PostCacheMapper postCacheMapper) {
        this.redisTemplate = redisTemplate;
        this.postCacheRedisRepository = postCacheRedisRepository;
        this.postCacheMapper = postCacheMapper;
    }

    public void incrementConcurrentPostViews(Long postId) {
        redisTemplate.opsForHash().increment(generateCachePostKey(postId), postCacheViewsField, 1);
    }

    public void incrementConcurrentPostLikes(Long postId) {
        redisTemplate.opsForHash().increment(generateCachePostKey(postId), postCacheLikesField, 1);
    }

    public void addCommentToPost(Long postId, Long commentId){
        var cacheKey = generateCachePostKey(postId);
        redisTemplate.opsForZSet().add(cacheKey, commentId, System.currentTimeMillis());

        var setSize = redisTemplate.opsForZSet().zCard(cacheKey);
        if (setSize != null && setSize > commentsInPostQuantity) {
            redisTemplate.opsForZSet().removeRange(cacheKey, 0, setSize - commentsInPostQuantity);
        }
    }

    public boolean existsById(Long postId){
        return postCacheRedisRepository.existsById(postId);
    }

    private String generateCachePostKey(Long postId) {
        return postCacheKeyPrefix + postId;
    }

    public List<PostCache> getPostCacheByIds(List<Long> postIds) {
        Iterable<PostCache> iterable = postCacheRedisRepository.findAllById(postIds);
        return StreamSupport.stream(iterable.spliterator(), false)
                .toList();
    }

    public void savePostCache(PostDto postDto) {
        var postCache = postCacheMapper.toPostCache(postDto);
        postCacheRedisRepository.save(postCache);
    }
}