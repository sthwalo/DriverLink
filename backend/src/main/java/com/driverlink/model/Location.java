package com.driverlink.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "locations")
@Data
@NoArgsConstructor
public class Location {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull(message = "Latitude is required")
  private Double latitude;

  @NotNull(message = "Longitude is required")
  private Double longitude;

  private String address;

  private String area;

  private String city;

  private String province;

  @Column(nullable = false)
  private boolean active = true;
}
