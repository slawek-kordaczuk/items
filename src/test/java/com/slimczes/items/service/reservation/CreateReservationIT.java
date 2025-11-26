package com.slimczes.items.service.reservation;

import com.slimczes.items.BaseIntegrationTest;
import com.slimczes.items.domain.event.ItemReservationFailed;
import com.slimczes.items.domain.event.ItemsReserved;
import com.slimczes.items.domain.model.Product;
import com.slimczes.items.domain.model.ProductReservationStatus;
import com.slimczes.items.domain.port.repository.ProductRepository;
import com.slimczes.items.service.reservation.dto.CreateReservationDto;
import com.slimczes.items.service.reservation.dto.ReservationItemDto;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static com.slimczes.items.TestcontainersConfiguration.getKafkaContainer;
import static com.slimczes.items.service.reservation.KafkaConsumer.createConsumer;
import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(properties = {
        "logging.level.org.apache.kafka=WARN"
})
public class CreateReservationIT extends BaseIntegrationTest {

    @Autowired
    private CreateReservation createReservation;

    @Autowired
    private ProductRepository productRepository;

    @Value("${kafka.topics.reserved}")
    private String reservedTopic;

    @Value("${kafka.topics.reserved-failed}")
    private String reservedFailedTopic;

    @Test
    void shouldReserveItemsSuccessfully() {
        // Given
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        CreateReservationDto reservationDto = new CreateReservationDto(
                orderId,
                customerId,
                List.of(new ReservationItemDto("TEST-001", "Test Product 1", 10))
        );

        try (Consumer<String, ItemsReserved> reservedConsumer = createConsumer(ItemsReserved.class, reservedTopic, getKafkaContainer().getBootstrapServers())) {
            // When
            createReservation.createReservation(reservationDto);
            // Then
            ConsumerRecords<String, ItemsReserved> records = reservedConsumer.poll(Duration.ofSeconds(5));
            assertThat(records).hasSize(1);

            ConsumerRecord<String, ItemsReserved> record = records.iterator().next();
            assertThat(record.topic()).isEqualTo(reservedTopic);

            ItemsReserved event = record.value();
            assertThat(event.orderId()).isEqualTo(orderId);
            assertThat(event.reservedItems()).hasSize(1);
            assertThat(event.reservedItems().getFirst().sku()).isEqualTo("TEST-001");
            assertThat(event.reservedItems().getFirst().reservedQuantity()).isEqualTo(10);
        }
        // And then
        Product updatedProduct = productRepository.findBySku("TEST-001").orElseThrow();
        assertThat(updatedProduct.getAvailableQuantity()).isEqualTo(90);
    }

    @Test
    void shouldReserveItemsFailed() {
        // Given
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        CreateReservationDto reservationDto = new CreateReservationDto(
                orderId,
                customerId,
                List.of(new ReservationItemDto("TEST-001", "Test Product 1", 110))
        );

        try (Consumer<String, ItemReservationFailed> failedConsumer = createConsumer(ItemReservationFailed.class, reservedFailedTopic, getKafkaContainer().getBootstrapServers())) {
            // When
            createReservation.createReservation(reservationDto);

            ConsumerRecords<String, ItemReservationFailed> records = failedConsumer.poll(Duration.ofSeconds(5));
            assertThat(records).hasSize(1);

            ConsumerRecord<String, ItemReservationFailed> record = records.iterator().next();
            assertThat(record.topic()).isEqualTo(reservedFailedTopic);

            ItemReservationFailed event = record.value();
            assertThat(event.orderId()).isEqualTo(orderId);
            assertThat(event.failedItems()).hasSize(1);
            assertThat(event.failedItems().getFirst().sku()).isEqualTo("TEST-001");
            assertThat(event.failedItems().getFirst().reason()).isEqualTo(ProductReservationStatus.NOT_AVAILABLE);
        }

        Product updatedProduct = productRepository.findBySku("TEST-001").orElseThrow();
        assertThat(updatedProduct.getAvailableQuantity()).isEqualTo(100);
    }

}
