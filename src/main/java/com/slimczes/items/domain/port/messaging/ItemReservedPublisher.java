package com.slimczes.items.domain.port.messaging;

import com.slimczes.items.domain.event.ItemReservationFailed;
import com.slimczes.items.domain.event.ItemsReserved;

public interface ItemReservedPublisher {

    void publishReservedItems(ItemsReserved itemsReserved);
    void publishReservedItemsFailed(ItemReservationFailed itemReservationFailed);

}
