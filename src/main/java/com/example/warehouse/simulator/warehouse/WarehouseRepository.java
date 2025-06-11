package com.example.warehouse.simulator.warehouse;

import com.example.warehouse.simulator.warehouse.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
}
