package com.example.warehouse.simulator.warehouse.controller;

import com.example.warehouse.simulator.shelf.model.request.CreateShelfCommand;
import com.example.warehouse.simulator.warehouse.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/warehouses/v1.0.0")
@RequiredArgsConstructor
public class WarehouseController implements WarehouseOperations {
    private final WarehouseService service;

    @Override
    @PostMapping("/")
    @ResponseStatus(CREATED)
    public void createWarehouse() {
        service.createEmptyWarehouse();
    }

    @Override
    @DeleteMapping("/{warehouseId}")
    @ResponseStatus(NO_CONTENT)
    public void deleteWarehouse(@PathVariable("warehouseId") long warehouseId) {
        service.deleteWarehouse(warehouseId);
    }

    @Override
    @GetMapping("/{warehouseId}")
    @ResponseStatus(OK)
    public void getWarehouse(@PathVariable("warehouseId") long warehouseId) {
        service.getWarehouse(warehouseId);
    }

    @Override
    @PostMapping("/{warehouseId}/{shelfId}")
    @ResponseStatus(CREATED)
    public void addShelf(@PathVariable("shelfId") long shelfId) {
        service.placeShelfAt(shelfId);
    }


}
