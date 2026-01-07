package com.mt.route;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;


@RegisterForReflection
public class RouteClientDto {

  @Data
  @Builder
  public static class PickupSearchRequest {

    private int limit;

    private Query query;

    @Data
    @Builder
    public static class Query {

      @JsonProperty("pickup_latest_datetime")
      private DateTimeRange pickupLatestDatetime;

      @JsonProperty("pickup_service_type")
      private InFilter<String> pickupServiceType;

      @JsonProperty("hub_id")
      private InFilter<Long> hubId;

      @JsonProperty("pickup_appointment_job_type")
      private InFilter<String> pickupAppointmentJobType;
    }

    @Data
    @Builder
    public static class DateTimeRange {

      @JsonProperty("lower_bound")
      private OffsetDateTime lowerBound;

      @JsonProperty("upper_bound")
      private OffsetDateTime upperBound;
    }

    @Data
    @Builder
    public static class InFilter<T> {

      private List<T> in;
    }
  }

  @RegisterForReflection
  public class PickUpRequest {

    @RegisterForReflection
    public record PostPickUpRequest(
        LocalDate date
    ) {

    }
  }

  @Data
  @NoArgsConstructor
  @RegisterForReflection
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class PickUpJobInfoResponse {

    Long id;

    String type;

    String status;

    List<PhotoPop> photos;

    @JsonProperty("tracking_ids")
    List<String> trackingIds;

    @JsonProperty("signature_url")
    String signatureUrl;

    @JsonProperty("failure_reason")
    FailReason failureReason;

    PickupInfoMeta metadata;

    @Data
    @NoArgsConstructor
    @RegisterForReflection
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class PickupInfoMeta {

      @JsonProperty("parcel_pickup_quantity")
      Long parcelPickupQuantity;

      @JsonProperty("parcel_quantity")
      Long parcelQuantity;

      @JsonProperty("driver_name")
      String driverName;
    }

    @Data
    @NoArgsConstructor
    @RegisterForReflection
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class PhotoPop {

      Long id;

      String url;
    }

    @Data
    @NoArgsConstructor
    @RegisterForReflection
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class FailReason {

      @JsonProperty("failure_reason_translations")
      String failureReasonTranslations;
    }
  }

  @RegisterForReflection
  public record PostPaJob(List<PaJob> data) {

    @RegisterForReflection
    record PaJob(
        @JsonProperty("pickup_appointment_job_id") Long pickupAppointmentJobId,
        @JsonProperty("pickup_service_type") String pickupServiceType,
        @JsonProperty("pickup_service_level") String pickupServiceLevel,
        @JsonProperty("pickup_appointment_job_type") String pickupAppointmentJobType,
        @JsonProperty("zone_type") String zoneType,
        @JsonProperty("name") String name,
        @JsonProperty("contact") String contact,
        @JsonProperty("email") String email,
        @JsonProperty("system_id") String systemId,
        @JsonProperty("shipper_id") Long shipperId,
        @JsonProperty("master_shipper_id") Long masterShipperId,
        @JsonProperty("pickup_ready_datetime") @JsonFormat(shape = JsonFormat.Shape.STRING,
                                                           pattern = "yyyy-MM-dd'T'HH:mm:ssX") OffsetDateTime pickupReadyDatetime,
        @JsonProperty("pickup_latest_datetime") @JsonFormat(shape = JsonFormat.Shape.STRING,
                                                            pattern = "yyyy-MM-dd'T'HH:mm:ssX") OffsetDateTime pickupLatestDatetime,
        @JsonProperty("service_datetime") @JsonFormat(shape = JsonFormat.Shape.STRING,
                                                      pattern = "yyyy-MM-dd'T'HH:mm:ssX") OffsetDateTime serviceDatetime,
        @JsonProperty("pickup_timeslot") String pickupTimeslot,
        @JsonProperty("pickup_instructions") String pickupInstructions,
        @JsonProperty("pickup_type") String pickupType,
        @JsonProperty("address_id") Long addressId,
        @JsonProperty("address_1") String address1,
        @JsonProperty("address_2") String address2,
        @JsonProperty("postcode") String postcode,
        @JsonProperty("neighbourhood") String neighbourhood,
        @JsonProperty("locality") String locality,
        @JsonProperty("region") String region,
        @JsonProperty("country") String country,
        @JsonProperty("latitude") Double latitude,
        @JsonProperty("longitude") Double longitude,
        @JsonProperty("status") String status,
        @JsonProperty("pickup_approx_volume") String pickupApproxVolume,
        @JsonProperty("failure_reason_code_id") Long failureReasonCodeId,
        @JsonProperty("failure_reason_id") Long failureReasonId,
        @JsonProperty("waypoint_id") Long waypointId,
        @JsonProperty("route_id") Long routeId,
        @JsonProperty("routing_zone_id") Long routingZoneId,
        @JsonProperty("hub_id") Long hubId,
        @JsonProperty("driver_id") Long driverId,
        @JsonProperty("driver_name") String driverName,
        @JsonProperty("dp_id") Long dpId,
        @JsonProperty("dp_partner_id") Long dpPartnerId,
        @JsonProperty("dp_name") String dpName,
        @JsonProperty("dp_address") String dpAddress,
        @JsonProperty("dp_contact_number") String dpContactNumber,
        @JsonProperty("dp_email") String dpEmail,
        @JsonProperty("created_at") @JsonFormat(shape = JsonFormat.Shape.STRING,
                                                pattern = "yyyy-MM-dd'T'HH:mm:ssX") OffsetDateTime createdAt,
        @JsonProperty("final_timeslot_start_datetime") @JsonFormat(shape = JsonFormat.Shape.STRING,
                                                                   pattern = "yyyy-MM-dd'T'HH:mm:ssX") OffsetDateTime finalTimeslotStartDatetime,
        @JsonProperty("final_timeslot_end_datetime") @JsonFormat(shape = JsonFormat.Shape.STRING,
                                                                 pattern = "yyyy-MM-dd'T'HH:mm:ssX") OffsetDateTime finalTimeslotEndDatetime,
        @JsonProperty("preferred_timeslot_start_datetime") @JsonFormat(shape = JsonFormat.Shape.STRING,
                                                                       pattern = "yyyy-MM-dd'T'HH:mm:ssX") OffsetDateTime preferredTimeslotStartDatetime,
        @JsonProperty("preferred_timeslot_end_datetime") @JsonFormat(shape = JsonFormat.Shape.STRING,
                                                                     pattern = "yyyy-MM-dd'T'HH:mm:ssX") OffsetDateTime preferredTimeslotEndDatetime,
        @JsonProperty("operating_hours_start_datetime") @JsonFormat(shape = JsonFormat.Shape.STRING,
                                                                    pattern = "yyyy-MM-dd'T'HH:mm:ssX") OffsetDateTime operatingHoursStartDatetime,
        @JsonProperty("operating_hours_end_datetime") @JsonFormat(shape = JsonFormat.Shape.STRING,
                                                                  pattern = "yyyy-MM-dd'T'HH:mm:ssX") OffsetDateTime operatingHoursEndDatetime,
        @JsonProperty("tag_ids") List<Long> tagIds
    ) {

    }
  }

  @Builder
  @RegisterForReflection
  public record PostPaJobRequest(
      Map<String, Map<String, List<?>>> query,
      int limit,
      int search_after
  ) {

  }

  @Builder
  @RegisterForReflection
  public static class Wrapper {

    GetRoutes.RouteInfo routeInfo;

    RouteClientDto.GetWaypoints.Waypoint waypoint;

    List<RouteClientDto.GetJob.Job> jobs;
  }

  @RegisterForReflection
  public record GetJob(List<Job> data) {

    @RegisterForReflection
    public record Job(long id,
                      long waypoint_id,
                      long job_id,
                      String job_type) {

    }
  }

  @RegisterForReflection
  public record GetWaypoints(List<Waypoint> data) {

    @RegisterForReflection
    record Waypoint(Long id,
                    Long route_id,
                    String waypoint_type,
                    String status) {

    }
  }

  @RegisterForReflection
  public record GetRoutes(List<RouteInfo> data) {

    @RegisterForReflection
    public record RouteInfo(
        long id,
        String system_id,
        long legacy_id,
        long driver_id,
        String vehicle_number,
        long zone_id,
        long hub_id,
        OffsetDateTime datetime,
        String date,
        boolean archived,
        OffsetDateTime estimated_end_time,
        OffsetDateTime driver_assign_time,
        int driver_attendance,
        String route_comments,
        String outbound_comments,
        Boolean is_ok,
        String status,
        String route_password,
        OffsetDateTime created_at,
        OffsetDateTime updated_at,
        OffsetDateTime deleted_at
    ) {

    }
  }

  @RegisterForReflection
  public record PickUpJobRequest(
      int limit,
      Query query
  ) {

    @RegisterForReflection
    public record Query(
        @JsonProperty("pickup_latest_datetime")
        Range pickupLatestDatetime,

        @JsonProperty("pickup_service_type")
        InList<String> pickupServiceType,

        @JsonProperty("hub_id")
        InList<Integer> hubId,

        @JsonProperty("pickup_appointment_job_type")
        InList<String> pickupAppointmentJobType
    ) {

    }

    @RegisterForReflection
    public record Range(
        @JsonProperty("lower_bound")
        String lower,

        @JsonProperty("upper_bound")
        String upper
    ) {

    }

    @RegisterForReflection
    public record InList<T>(
        List<T> in
    ) {

    }
  }

  @RegisterForReflection
  public record PickUpJobResponse(
      @JsonProperty("data") List<PickUpJob> data,
      @JsonProperty("has_more") boolean hasMore,
      @JsonProperty("last_hit_sort_value") List<Long> lastHitSortValue
  ) {

    @RegisterForReflection
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record PickUpJob(
        @JsonProperty("pickup_appointment_job_id") Long pickupAppointmentJobId,
        @JsonProperty("pickup_service_type") String pickupServiceType,
        @JsonProperty("pickup_service_level") String pickupServiceLevel,
        @JsonProperty("pickup_appointment_job_type") String pickupAppointmentJobType,
        @JsonProperty("zone_type") String zoneType,
        @JsonProperty("name") String name,
        @JsonProperty("contact") String contact,
        @JsonProperty("email") String email,
        @JsonProperty("system_id") String systemId,
        @JsonProperty("shipper_id") Long shipperId,
        @JsonProperty("master_shipper_id") Long masterShipperId,
        @JsonProperty("pickup_ready_datetime") @JsonFormat(shape = JsonFormat.Shape.STRING,
                                                           pattern = "yyyy-MM-dd'T'HH:mm:ssX") OffsetDateTime pickupReadyDatetime,
        @JsonProperty("pickup_latest_datetime") @JsonFormat(shape = JsonFormat.Shape.STRING,
                                                            pattern = "yyyy-MM-dd'T'HH:mm:ssX") OffsetDateTime pickupLatestDatetime,
        @JsonProperty("service_datetime") @JsonFormat(shape = JsonFormat.Shape.STRING,
                                                      pattern = "yyyy-MM-dd'T'HH:mm:ssX") OffsetDateTime serviceDatetime,
        @JsonProperty("pickup_timeslot") String pickupTimeslot,
        @JsonProperty("pickup_instructions") String pickupInstructions,
        @JsonProperty("pickup_type") String pickupType,
        @JsonProperty("address_id") Long addressId,
        @JsonProperty("address_1") String address1,
        @JsonProperty("address_2") String address2,
        @JsonProperty("postcode") String postcode,
        @JsonProperty("neighbourhood") String neighbourhood,
        @JsonProperty("locality") String locality,
        @JsonProperty("region") String region,
        @JsonProperty("country") String country,
        @JsonProperty("latitude") Double latitude,
        @JsonProperty("longitude") Double longitude,
        @JsonProperty("status") String status,
        @JsonProperty("pickup_approx_volume") String pickupApproxVolume,
        @JsonProperty("failure_reason_code_id") Long failureReasonCodeId,
        @JsonProperty("failure_reason_id") Long failureReasonId,
        @JsonProperty("waypoint_id") Long waypointId,
        @JsonProperty("route_id") Long routeId,
        @JsonProperty("routing_zone_id") Long routingZoneId,
        @JsonProperty("hub_id") Long hubId,
        @JsonProperty("driver_id") Long driverId,
        @JsonProperty("driver_name") String driverName,
        @JsonProperty("dp_id") Long dpId,
        @JsonProperty("dp_partner_id") Long dpPartnerId,
        @JsonProperty("dp_name") String dpName,
        @JsonProperty("dp_address") String dpAddress,
        @JsonProperty("dp_contact_number") String dpContactNumber,
        @JsonProperty("dp_email") String dpEmail,
        @JsonProperty("created_at") @JsonFormat(shape = JsonFormat.Shape.STRING,
                                                pattern = "yyyy-MM-dd'T'HH:mm:ssX") OffsetDateTime createdAt,
        @JsonProperty("final_timeslot_start_datetime") @JsonFormat(shape = JsonFormat.Shape.STRING,
                                                                   pattern = "yyyy-MM-dd'T'HH:mm:ssX") OffsetDateTime finalTimeslotStartDatetime,
        @JsonProperty("final_timeslot_end_datetime") @JsonFormat(shape = JsonFormat.Shape.STRING,
                                                                 pattern = "yyyy-MM-dd'T'HH:mm:ssX") OffsetDateTime finalTimeslotEndDatetime,
        @JsonProperty("preferred_timeslot_start_datetime") @JsonFormat(shape = JsonFormat.Shape.STRING,
                                                                       pattern = "yyyy-MM-dd'T'HH:mm:ssX") OffsetDateTime preferredTimeslotStartDatetime,
        @JsonProperty("preferred_timeslot_end_datetime") @JsonFormat(shape = JsonFormat.Shape.STRING,
                                                                     pattern = "yyyy-MM-dd'T'HH:mm:ssX") OffsetDateTime preferredTimeslotEndDatetime,
        @JsonProperty("operating_hours_start_datetime") @JsonFormat(shape = JsonFormat.Shape.STRING,
                                                                    pattern = "yyyy-MM-dd'T'HH:mm:ssX") OffsetDateTime operatingHoursStartDatetime,
        @JsonProperty("operating_hours_end_datetime") @JsonFormat(shape = JsonFormat.Shape.STRING,
                                                                  pattern = "yyyy-MM-dd'T'HH:mm:ssX") OffsetDateTime operatingHoursEndDatetime,
        @JsonProperty("tag_ids") List<Long> tagIds
    ) {

    }

  }
}
