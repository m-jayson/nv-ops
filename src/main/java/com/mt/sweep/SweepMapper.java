package com.mt.sweep;


import com.mt.order.OrderClientApi.OrderSearchResponse.SearchData.Order;
import com.mt.sweep.SweepClientApi.SweepRequest;
import com.mt.sweep.SweepClientApi.SweepResponse;
import com.mt.sweep.SweepClientApi.SweepResponse.ParcelRoutingData;
import com.mt.sweep.SweepService.SweepOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;


@Mapper(
    componentModel = "cdi",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface SweepMapper {

  @Mapping(target = "hub_id", expression = "java(sweepConfig.hubId())")
  @Mapping(target = "scan", source = "trackingId")
  @Mapping(target = "task_id", expression = "java(sweepConfig.taskId())")
  @Mapping(target = "node_id", expression = "java(sweepConfig.nodeId())")
  @Mapping(target = "to_return_dp_id", expression = "java(sweepConfig.toReturnDpId())")
  @Mapping(
      target = "hub_user",
      expression = "java(sweepConfig.hubUser().orElse(null))"
  )
  SweepRequest toSweepRequest(
      SweepConfig sweepConfig,
      String trackingId
  );

  @Mapping(target = "status", source = "sweepResponse.granularStatus")
  @Mapping(target = "responsibleHubName", source = "sweepData.responsibleHubName")
  @Mapping(target = "zoneName", source = "sweepData.zoneName")
  @Mapping(target = "rackSector", source = "sweepData.rackSector")
  @Mapping(target = "nextNode", source = "sweepData.nextNode")
  @Mapping(target = "tags", source = "sweepData.tags")
  @Mapping(target = "isRts", source = "sweepData.rtsed")

  @Mapping(target = "order.shipperId", source = "order.shipper_id")
  @Mapping(target = "order.shipperName", source = "order.to_name")
  @Mapping(target = "order.trackingId", source = "order.tracking_id")
  @Mapping(target = "order.address1", source = "order.to_address1")
  @Mapping(target = "order.address2", source = "order.to_address2")
  @Mapping(target = "order.city", source = "order.to_city")
  SweepOrder toSweepOrder (
      SweepResponse sweepResponse,
      ParcelRoutingData sweepData,
      Order order
  );
}
