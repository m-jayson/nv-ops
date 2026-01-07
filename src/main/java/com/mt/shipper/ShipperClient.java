package com.mt.shipper;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.mt.common.AuthorizationBearerFilter;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.Builder;
import lombok.Data;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;


@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "nv-client")
@RegisterProvider(AuthorizationBearerFilter.class)
public interface ShipperClient {

  @POST
  @Path("/shipper-search/2.0/shippers/search")
  Uni<ShipperSearchResponse> searchShippers (
      @QueryParam("from") int from,
      @QueryParam("size") int size,
      ShipperSearchRequest request
  );


  @Data
  @Builder
  class ShipperSearchRequest {

    @JsonProperty("search_filters")
    private List<SearchFilter> searchFilters;

    @JsonProperty("search_field")
    private SearchField searchField;

    @Data
    @Builder
    public static class SearchFilter {

      private String field;

      private List<String> values;
    }

    @Data
    @Builder
    public static class SearchField {

      @JsonProperty("match_type")
      private String matchType;

      private List<String> fields;

      private String value;
    }
  }

  @Data
  class ShipperSearchResponse {

    @JsonProperty("requested_from")
    private Integer requestedFrom;

    @JsonProperty("requested_search_after")
    private Integer requestedSearchAfter;

    @JsonProperty("requested_size")
    private Integer requestedSize;

    @JsonProperty("total_hits")
    private Integer totalHits;

    @JsonProperty("total_size")
    private Integer totalSize;

    @JsonProperty("returned_size")
    private Integer returnedSize;

    private List<Detail> details;

    @Data
    public static class Detail {

      private Long id;

      @JsonProperty("legacy_id")
      private Long legacyId;

      @JsonProperty("external_id")
      private String externalId;

      private String country;

      private String name;

      @JsonProperty("short_name")
      private String shortName;

      private Boolean active;

      private Boolean archived;

      private String email;

      private String contact;

      @JsonProperty("sales_person")
      private String salesPerson;

      @JsonProperty("liaison_address")
      private String liaisonAddress;

      @JsonProperty("billing_name")
      private String billingName;

      @JsonProperty("billing_contact")
      private String billingContact;

      @JsonProperty("billing_address")
      private String billingAddress;

      @JsonProperty("billing_postcode")
      private String billingPostcode;

      @JsonProperty("industry_id")
      private Integer industryId;

      @JsonProperty("distribution_channel_id")
      private Integer distributionChannelId;

      @JsonProperty("account_type_id")
      private Integer accountTypeId;

      @JsonProperty("dash_account_creation")
      private Boolean dashAccountCreation;
    }
  }
}
