package com.mt.sweep;


import com.mt.common.AuditListener;
import com.mt.common.Auditable;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;


@Getter
@Setter
@Entity
@Builder
@Table(name = "sweeper_live")
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditListener.class)
public class Sweep extends Auditable {

  @Id
  String trackingNumber;

  String type;

  String status;
}
