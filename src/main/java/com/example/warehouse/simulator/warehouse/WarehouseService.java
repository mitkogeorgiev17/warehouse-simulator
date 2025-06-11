package com.example.warehouse.simulator.warehouse;

import com.example.warehouse.simulator.robot.RobotRepository;
import com.example.warehouse.simulator.robot.model.Robot;
import com.example.warehouse.simulator.shelf.ShelfRepository;
import com.example.warehouse.simulator.shelf.model.Shelf;
import com.example.warehouse.simulator.warehouse.model.Warehouse;
import com.example.warehouse.simulator.warehouse.utils.GridUtils;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;

@Service
@Slf4j
@RequiredArgsConstructor
public class WarehouseService {

    @Value("${warehouse.width}")
    private int width;

    @Value("${warehouse.height}")
    private int height;

    private final ShelfRepository shelfRepository;

    private final RobotRepository robotRepository;

    private final WarehouseRepository repository;

    public void createEmptyWarehouse() {
        log.info("Creating empty warehouse");
        List<List<String>> grid = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            List<String> row = new ArrayList<>();
            for (int x = 0; x < width; x++) {
                if (y == 0 || y == height - 1 || x == 0 || x == width - 1) {
                    row.add("-");
                } else {
                    row.add(".");
                }
            }
            grid.add(row);
        }
        Warehouse warehouse = new Warehouse();
        warehouse.setGrid(grid);
        repository.save(warehouse);
        log.info("Empty warehouse created with dimensions {}x{}", width, height);
    }

    public void getWarehouse(long warehouseId) {
        Warehouse warehouse = repository.findAll().stream().findFirst()
                .orElseThrow(() -> new EntityNotFoundException("No warehouse found."));

        List<List<String>> grid = warehouse.getGrid();
        if (grid == null || grid.isEmpty()) {
            log.info("Warehouse is empty.");
            return;
        }

        for (List<String> row : grid) {
            log.info(String.join(" ", row));
        }
    }

    public boolean canMoveRobotTo(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height &&
                !shelfRepository.existsByLocationXAndLocationY(x, y);
    }

    public void placeShelfAt(long shelfId) {
        Shelf shelf = shelfRepository.findById(shelfId)
            .orElseThrow(() -> new EntityNotFoundException("Shelf with ID " + shelfId + " not found."));
        int x = shelf.getLocationX();
        int y = shelf.getLocationY();

        Warehouse warehouse = repository.findAll().stream().findFirst()
                .orElseThrow(() -> new EntityNotFoundException("No warehouse found."));
        List<List<String>> grid = warehouse.getGrid();

        if (!GridUtils.isCellAvailableForPlacement(grid, x, y)) {
            throw new IllegalArgumentException("Cannot place shelf at coordinates (" + x + ", " + y + ")");
        }
     
        grid.get(y).set(x, String.valueOf(shelf.getId()));
        warehouse.setGrid(grid);
        repository.save(warehouse);
        log.info("Placed shelf at coordinates ({}, {})", x, y);
    }

    public void markRobotBaseOnGrid(Robot robot) {
        Warehouse warehouse = repository.findAll().stream().findFirst()
            .orElseThrow(() -> new EntityNotFoundException("No warehouse found."));

        List<List<String>> grid = warehouse.getGrid();

        if (!GridUtils.isCellAvailableForPlacement(grid, robot.getBaseX(), robot.getBaseY())) {
            throw new IllegalArgumentException("Cannot place shelf at coordinates (" + robot.getBaseX() + ", " + robot.getBaseY() + ")");
        }

        grid.get(robot.getBaseY()).set(robot.getBaseX(), "B");
        
        warehouse.setGrid(grid);
        repository.save(warehouse);
    }

    public void deleteWarehouse(long warehouseId) {
        log.info("Deleting warehouse by ID {}.", warehouseId);

        if (!repository.existsById(warehouseId)) {
            throw new EntityNotFoundException("Shelf with ID " + warehouseId + " not found.");
        }

        repository.deleteById(warehouseId);

        log.info("Warehouse deleted successfully.");
    }

}
