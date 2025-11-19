package com.slimczes.items.service.reservation;

import com.slimczes.items.BaseIntegrationTest;
import com.slimczes.items.domain.event.ItemsReserved;
import com.slimczes.items.domain.model.Product;
import com.slimczes.items.domain.port.repository.ProductRepository;
import com.slimczes.items.service.reservation.dto.CancelReservationDto;
import com.slimczes.items.service.reservation.dto.CreateReservationDto;
import com.slimczes.items.service.reservation.dto.ReservationItemDto;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static com.slimczes.items.TestcontainersConfiguration.getKafkaContainer;
import static com.slimczes.items.service.reservation.KafkaConsumer.createConsumer;
import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(properties = {
        "logging.level.org.apache.kafka=WARN"
})
@Transactional
public class CancelReservationIT extends BaseIntegrationTest {

    @Autowired
    private CreateReservation createReservation;

    @Autowired
    private CancelReservation cancelReservation;

    @Autowired
    private ProductRepository productRepository;

    @Value("${kafka.topics.reserved}")
    private String reservedTopic;

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
        try (Consumer<String, ItemsReserved> reservedConsumer = createConsumer(ItemsReserved.class, reservedTopic, getKafkaContainer().getBootstrapServers())) {
            createReservation.createReservation(reservationDto);
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
        cancelReservation.cancelReservation(cancelReservationDto);

        // Then
        Product productAfterCancellation = productRepository.findBySku("TEST-002").orElseThrow();
        assertThat(productAfterCancellation.getAvailableQuantity()).isEqualTo(50);
    }
}
