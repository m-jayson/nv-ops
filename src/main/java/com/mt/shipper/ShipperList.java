package com.mt.shipper;


import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

@Getter
@Entity
@Table(name = "shipper_list", schema = "public")
@Immutable
public class ShipperList extends PanacheEntityBase {

  @Id
  @Column(name = "shipperid", nullable = false)
  private Long shipperId;
}
