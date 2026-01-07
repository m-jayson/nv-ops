package com.mt.shipper;


import com.mt.shipper.ShipperClient.ShipperSearchRequest;
import com.mt.shipper.ShipperClient.ShipperSearchRequest.SearchField;
import com.mt.shipper.ShipperClient.ShipperSearchRequest.SearchFilter;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.Instant;
import java.util.List;
import java.util.Objects;


@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShipperService {

  @NonFinal
  @RestClient
  ShipperClient shipperClient;

  ShipperRepository shipperRepository;


  public Uni<Void> fetchShipper () {

    return this.shipperRepository.findAllShippersView()
        .onItem().transformToMulti(Multi.createFrom()::iterable)
        .onItem().transformToUniAndMerge(shipper -> {
          return this.fetchShipperInfoBulk(shipper.getShipperId());
        }).collect().asList()
        .onItem().transformToUni(this.shipperRepository::bulkSave);
  }


  public Uni<Shipper> fetchShipperInfoBulk (Long shipperId) {

    return this.shipperClient.searchShippers(
            0, 100, ShipperSearchRequest.builder()
                .searchField(SearchField.builder()
                                 .matchType("default")
                                 .fields(List.of("global_id", "id", "email"))
                                 .value(String.valueOf(shipperId))
                                 .build())
                .searchFilters(List.of(SearchFilter.builder()
                                           .field("system_id")
                                           .values(List.of("ph"))
                                           .build()))
                .build()
        ).map(ShipperClient.ShipperSearchResponse::getDetails)
        .onItem().ifNull().continueWith(List.of())
        .onItem().transformToUni(x -> {
          return x.stream()
              .filter(a -> Objects.equals(a.getId(), shipperId))
              .findFirst().map(b -> {
                var shipper = Shipper.builder()
                    .shipperId(b.getId())
                    .shipperAddress(b.getBillingAddress())
                    .name(b.getName())
                    .active(true)
                    .build();
                shipper.setLastUpdatedOn(Instant.now());
                return Uni.createFrom().item(shipper);
              })
              .orElse(Uni.createFrom().nullItem());
        });
  }
}
