package com.example.warehouse.simulator.warehouse.controller;

import com.example.warehouse.simulator.shelf.model.request.CreateShelfCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Warehouse Operations", description = "Operations for managing warehouse")
public interface WarehouseOperations {

    @Operation(summary = "Create a new empty warehouse", description = "Initialize a new warehouse with a grid layout")
    void createWarehouse();

    @Operation(summary = "Delete a warehouse", description = "Remove an existing warehouse")
    void deleteWarehouse(long warehouseId);

    @Operation(summary = "Get warehouse layout", description = "Get the current layout of the warehouse")
    void getWarehouse(long warehouseId);

    @Operation(summary = "Add a shelf to the warehouse", description = "Add a new shelf to the warehouse layout")
    void addShelf(long shelfId);
}