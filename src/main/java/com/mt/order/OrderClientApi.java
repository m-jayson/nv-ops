package com.mt.order;


import com.mt.common.AuthorizationBearerFilter;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.math.BigDecimal;
import java.util.List;


@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "nv-client")
@RegisterProvider(AuthorizationBearerFilter.class)
public interface OrderClientApi {

  @POST
  @Path("/order-search/search/masked")
  Uni<OrderSearchResponse> search (OrderSearchRequest request);


  record OrderSearchRequest(
      SearchField search_field,
      Object search_range,
      List<Object> search_filters
  ) {

  }

  record SearchField(
      List<String> fields,
      String match_type,
      String value
  ) {

  }

  record OrderSearchResponse(
      int total,
      String max_score,
      List<SearchData> search_data
  ) {

    public record SearchData(
        String score,
        Order order
    ) {

      public record Order(
          long id,
          String tracking_id,
          String x_dock_tracking_id,
          String mps_tracking_id,

          String from_name,
          String from_contact,
          String from_email,
          String from_address1,
          String from_address2,
          String from_postcode,

          String to_name,
          String to_contact,
          String to_email,
          String to_address1,
          String to_address2,
          String to_postcode,
          String to_city,
          String to_district,
          String to_state,

          String granular_status,
          long created_at,
          String stamp_id,
          String status,

          long shipper_id,
          long global_shipper_id,
          String type,

          BigDecimal cod,

          String bulk_id,
          Integer bulk_sequence_number,
          String mps_id,
          Integer mps_sequence_number,

          String shipper_order_ref_no,
          boolean is_rts,
          int source_id,

          Long completed_at,
          String service_type,
          String service_level,
          String sort_code,
          String to_3pl,

          Long parent_shipper_id,
          String batch_id,

          BigDecimal cod_converted_amount,
          String cod_currency_source,
          String cod_currency_target,

          Boolean mps_documents_required,
          Boolean is_mps_document
      ) {

      }
    }
  }
}
