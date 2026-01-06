package com.mt.shipper;


import com.mt.common.AuditListener;
import com.mt.common.Auditable;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;


@Getter
@Setter
@Entity
@Builder
@Table(name = "shipper")
@EntityListeners(AuditListener.class)
@NoArgsConstructor
@AllArgsConstructor
public class Shipper extends Auditable {

  @Id
  @GeneratedValue
  @UuidGenerator
  UUID id;

  String name;

  Long shipperId;

  Long masterShipperId;

  String address1;

  String address2;

  String postcode;

  String neighbourhood;

  String locality;

  String region;

  String country;

  Double latitude;

  Double longitude;

}
