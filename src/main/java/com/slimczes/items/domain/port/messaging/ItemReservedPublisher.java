package com.slimczes.items.domain.port.messaging;

import com.slimczes.items.domain.event.ItemReservationFailed;
import com.slimczes.items.domain.event.ItemsReserved;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public interface ItemReservedPublisher {

    void publishReservedItems(ItemsReserved itemsReserved);
    void publishReservedItemsFailed(ItemReservationFailed itemReservationFailed);

}
