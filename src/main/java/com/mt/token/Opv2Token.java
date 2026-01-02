package com.mt.token;


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
@Table(name = "opv2_token")
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditListener.class)
public class Opv2Token extends Auditable {

  @Id
  @GeneratedValue
  @UuidGenerator
  UUID id;

  String bearerToken;

  boolean inUse;
}
