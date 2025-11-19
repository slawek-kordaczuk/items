package com.slimczes.items.service.reservation;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.slimczes.items.TestcontainersConfiguration.getKafkaContainer;

class KafkaConsumer {

    static <T> Consumer<String, T> createConsumer(Class<T> targetType, String topic, String bootstrapServers) {
        Map<String, Object> props = createConsumerProps(bootstrapServers, targetType);
        DefaultKafkaConsumerFactory<String, T> factory =
                new DefaultKafkaConsumerFactory<>(props);
        Consumer<String, T> consumer = factory.createConsumer();
        consumer.subscribe(List.of(topic));
        consumer.poll(Duration.ofMillis(1000));
        return consumer;
    }

   private static Map<String, Object> createConsumerProps(String bootstrapServers, Class<?> targetType) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group-" + UUID.randomUUID());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, targetType.getName());
        return props;
    }

}
