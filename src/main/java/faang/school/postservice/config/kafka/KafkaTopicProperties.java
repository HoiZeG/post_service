package faang.school.postservice.config.kafka;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "spring.data.kafka")
public class KafkaTopicProperties {
    private List<Topic> topics;
    @Value("${spring.data.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Data
    public static class Topic {
        private String name;
        private int partitions;
        private short replicas;
    }
}