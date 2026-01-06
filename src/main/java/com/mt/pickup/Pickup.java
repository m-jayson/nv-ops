package com.mt.pickup;


import com.mt.common.AuditListener;
import com.mt.common.Auditable;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;


@Getter
@Setter
@Entity
@Builder
@Table(name = "pickup")
@EntityListeners(AuditListener.class)
@NoArgsConstructor
@AllArgsConstructor
public class Pickup extends Auditable {

  @Id
  @GeneratedValue
  @UuidGenerator
  @Column(updatable = false, nullable = false)
  UUID id;

  Long waypointId;

  Long routeId;

  Long jobId;

  LocalDate pickUpDate;

  Long driverId;

  Long shipperId;

  String status;

  @ElementCollection
  @CollectionTable(
      name = "pickup_result",
      joinColumns = @JoinColumn(name = "pickup_id")
  )
  @MapKeyColumn(name = "result")
  @Column(name = "count")
  Map<String, Integer> pickUpResult;
}
