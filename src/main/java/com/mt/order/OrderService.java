package com.mt.order;


import com.mt.order.OrderClientApi.OrderSearchResponse.SearchData;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

import static com.mt.order.OrderClientApi.OrderSearchRequest;
import static com.mt.order.OrderClientApi.SearchField;


@ApplicationScoped
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class OrderService {

  @NonFinal
  @RestClient
  OrderClientApi orderClientApi;


  public Uni<SearchData.Order> findByTrackingId (String trackingId) {

    var request = new OrderSearchRequest(
        new SearchField(
            List.of("tracking_id", "stamp_id", "x_dock_tracking_id"),
            "full_text",
            trackingId
        ),
        null,
        List.of()
    );

    return orderClientApi.search(request)
        .onItem().ifNotNull()
        .transform(resp -> resp.search_data().getFirst().order());
  }
}
