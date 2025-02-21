package com.driverlink.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LocationDTO {
    private Long id;
    
    @NotNull(message = "Latitude is required")
    private Double latitude;
    
    @NotNull(message = "Longitude is required")
    private Double longitude;
    
    private String address;
    private String area;
    private String city;
    private String province;
}
