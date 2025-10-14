package com.slimczes.items.service.reservation;

import com.slimczes.items.TestcontainersConfiguration;
import com.slimczes.items.domain.event.ItemReservationFailed;
import com.slimczes.items.domain.event.ItemsReserved;
import com.slimczes.items.domain.model.Product;
import com.slimczes.items.domain.model.ReservationStatus;
import com.slimczes.items.domain.port.repository.ProductRepository;
import com.slimczes.items.service.reservation.dto.CancelReservationDto;
import com.slimczes.items.service.reservation.dto.CreateReservationDto;
import com.slimczes.items.service.reservation.dto.ReservationItemDto;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.kafka.KafkaContainer;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@TestPropertySource(properties = {
        "logging.level.org.apache.kafka=WARN"
})
@Transactional
public class ReservationServiceIT {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private KafkaContainer kafkaContainer;

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

        try (Consumer<String, ItemsReserved> reservedConsumer = createReservedConsumer()) {
            // When
            reservationService.createReservation(reservationDto);

            ConsumerRecords<String, ItemsReserved> records = reservedConsumer.poll(Duration.ofSeconds(5));
            assertThat(records).hasSize(1);

            ConsumerRecord<String, ItemsReserved> record = records.iterator().next();
            assertThat(record.topic()).isEqualTo(reservedTopic);
            assertThat(record.key()).isEqualTo(orderId.toString());

            ItemsReserved event = record.value();
            assertThat(event.orderId()).isEqualTo(orderId);
            assertThat(event.reservedItems()).hasSize(1);
            assertThat(event.reservedItems().getFirst().sku()).isEqualTo("TEST-001");
            assertThat(event.reservedItems().getFirst().reservedQuantity()).isEqualTo(10);
        }

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

        try (Consumer<String, ItemReservationFailed> failedConsumer = createFailedConsumer()) {
            // When
            reservationService.createReservation(reservationDto);

            ConsumerRecords<String, ItemReservationFailed> records = failedConsumer.poll(Duration.ofSeconds(5));
            assertThat(records).hasSize(1);

            ConsumerRecord<String, ItemReservationFailed> record = records.iterator().next();
            assertThat(record.topic()).isEqualTo(reservedFailedTopic);
            assertThat(record.key()).isEqualTo(orderId.toString());

            ItemReservationFailed event = record.value();
            assertThat(event.orderId()).isEqualTo(orderId);
            assertThat(event.failedItems()).hasSize(1);
            assertThat(event.failedItems().getFirst().sku()).isEqualTo("TEST-001");
            assertThat(event.failedItems().getFirst().reason()).isEqualTo(ReservationStatus.NOT_AVAILABLE);
        }

        Product updatedProduct = productRepository.findBySku("TEST-001").orElseThrow();
        assertThat(updatedProduct.getAvailableQuantity()).isEqualTo(100);
    }

    @Test
    void shouldCancelReservationSuccessfully() {
        // Given
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        CreateReservationDto reservationDto = new CreateReservationDto(
                orderId,
                customerId,
                List.of(new ReservationItemDto("TEST-002", "Test Product 2", 20))
        );
        try (Consumer<String, ItemsReserved> reservedConsumer = createReservedConsumer()) {
            reservationService.createReservation(reservationDto);
            ConsumerRecords<String, ItemsReserved> records = reservedConsumer.poll(Duration.ofSeconds(5));
            assertThat(records).hasSize(1);
            Product productAfterReservation = productRepository.findBySku("TEST-002").orElseThrow();
            assertThat(productAfterReservation.getAvailableQuantity()).isEqualTo(30);
        }
        CancelReservationDto cancelReservationDto = new CancelReservationDto(
                orderId,
                customerId,
                "Order cancelled by customer",
                List.of(new ReservationItemDto("TEST-002", "Test Product 2", 20))
        );

        // When
        reservationService.cancelReservation(cancelReservationDto);

        // Then
        Product productAfterCancellation = productRepository.findBySku("TEST-002").orElseThrow();
        assertThat(productAfterCancellation.getAvailableQuantity()).isEqualTo(50);
    }

    private Consumer<String, ItemsReserved> createReservedConsumer() {
        String bootstrapServers = kafkaContainer.getBootstrapServers();
        Map<String, Object> props = createConsumerProps(bootstrapServers, ItemsReserved.class);
        DefaultKafkaConsumerFactory<String, ItemsReserved> factory =
                new DefaultKafkaConsumerFactory<>(props);
        Consumer<String, ItemsReserved> consumer = factory.createConsumer();
        consumer.subscribe(List.of(reservedTopic));
        consumer.poll(Duration.ofMillis(1000));
        return consumer;
    }

    private Consumer<String, ItemReservationFailed> createFailedConsumer() {
        String bootstrapServers = kafkaContainer.getBootstrapServers();
        Map<String, Object> props = createConsumerProps(bootstrapServers, ItemReservationFailed.class);
        DefaultKafkaConsumerFactory<String, ItemReservationFailed> factory =
                new DefaultKafkaConsumerFactory<>(props);
        Consumer<String, ItemReservationFailed> consumer = factory.createConsumer();
        consumer.subscribe(List.of(reservedFailedTopic));
        consumer.poll(Duration.ofMillis(1000));
        return consumer;
    }

    private Map<String, Object> createConsumerProps(String bootstrapServers, Class<?> targetType) {
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
