package com.slimczes.items.adapter.messaging.out;

import com.slimczes.items.domain.event.ItemReservationFailed;
import com.slimczes.items.domain.event.ItemsReserved;
import com.slimczes.items.domain.port.messaging.ItemReservedPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Component
@RequiredArgsConstructor
@Slf4j
class ItemReservedKafkaPublisher implements ItemReservedPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.reserved}")
    private String itemsReservedTopic;

    @Value("${kafka.topics.reserved-failed}")
    private String itemsReservedFailedTopic;

    @Override
    public void publishReservedItems(ItemsReserved itemsReserved) {
        log.info("Publishing items reserved event: {}", itemsReserved);
        kafkaTemplate.send(itemsReservedTopic, itemsReserved)
                     .whenComplete((result, exception) -> {
                         if (exception != null) {
                             log.error("Error publishing items reserved event: {}", exception.getMessage());
                         } else {
                             log.info("Items reserved event published successfully: {}", result.getProducerRecord());
                         }
                     });
    }

    @Override
    public void publishReservedItemsFailed(ItemReservationFailed itemReservationFailed) {
        log.info("Publishing items reservation failed event: {}", itemReservationFailed);
        kafkaTemplate.send(itemsReservedFailedTopic, itemReservationFailed)
                     .whenComplete((result, exception) -> {
                         if (exception != null) {
                             log.error("Error publishing items reservation failed event: {}", exception.getMessage());
                         } else {
                             log.info("Items reservation failed event published successfully: {}", result.getProducerRecord());
                         }
                     });
    }
}
