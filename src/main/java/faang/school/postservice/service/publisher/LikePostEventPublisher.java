package faang.school.postservice.service.publisher;

import faang.school.postservice.dto.event.LikePostEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LikePostEventPublisher implements MessagePublisher<LikePostEvent> {

    private final RedisTemplate redisTemplate;
    private final ChannelTopic channelTopic;

    public LikePostEventPublisher(RedisTemplate redisTemplate,
                                  @Qualifier("likePostEventTopic") ChannelTopic channelTopic) {
        this.redisTemplate = redisTemplate;
        this.channelTopic = channelTopic;
    }

    @Override
    public void publish(LikePostEvent message) {
        redisTemplate.convertAndSend(channelTopic.getTopic(), message);
        log.info("Message was send {}, in topic - {}", message, channelTopic.getTopic());
    }
}
