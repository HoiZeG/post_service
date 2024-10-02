package faang.school.postservice.producer.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.producer.Producer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

@AllArgsConstructor
@Slf4j
public class AbstractRedisProducer<E> implements Producer<E> {
    protected final RedisTemplate<String, Object> redisTemplate;
    protected final ObjectMapper objectMapper;
    protected final String topic;

    public void send(E event) {
        String message = convertToMessage(event);
        sendMessage(message);
    }

    protected String convertToMessage(E event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            log.error(
                    String.format("An error occurred while serializing the event: %s.", event), e
            );
            throw new RuntimeException("Error serializing event", e);
        }
    }

    protected void sendMessage(String message) {
        redisTemplate.convertAndSend(topic, message);
    }
}