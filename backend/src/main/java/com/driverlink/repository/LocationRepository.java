package com.driverlink.repository;

import com.driverlink.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findByCityAndActive(String city, boolean active);
    List<Location> findByProvinceAndActive(String province, boolean active);
}
