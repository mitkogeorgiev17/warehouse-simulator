package com.example.warehouse.simulator.event;

import com.example.warehouse.simulator.order.model.Order;
import com.example.warehouse.simulator.product.model.Product;
import com.example.warehouse.simulator.robot.model.Robot;
import com.example.warehouse.simulator.shelf.ShelfRepository;
import com.example.warehouse.simulator.shelf.model.Shelf;
import com.example.warehouse.simulator.warehouse.WarehouseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkerService {
    private final WarehouseUtils warehouse;
    private final ShelfRepository shelfRepository;

    public void doJob(Robot robot, Order order) {
        log.info("Robot {} starting job for order {} with {} products",
                robot.getSerialNumber(), order.getId(), order.getItems().size());

        // Visit each product location
        for (var entry : order.getItems().entrySet()) {
            Product product = entry.getKey();
            Long productId = product.getId();
            int quantityNeeded = entry.getValue().intValue();

            log.info("Robot {} collecting {} units of product {}",
                    robot.getSerialNumber(), quantityNeeded, productId);

            // Find shelf with this product
            Shelf targetShelf = findShelfWithProduct(productId);
            if (targetShelf == null) {
                log.warn("No shelf found with product ID {}, skipping", productId);
                continue;
            }

            // Check if shelf has enough quantity
            if (targetShelf.getQuantity() < quantityNeeded) {
                log.warn("Shelf only has {} units, but need {}. Taking what's available.",
                        targetShelf.getQuantity(), quantityNeeded);
            }

            // Calculate path from robot's current position to shelf
            List<Point> pathToShelf = findSimplePath(
                    robot.getLocationX(), robot.getLocationY(),
                    targetShelf.getLocationX(), targetShelf.getLocationY()
            );

            // Move robot to the shelf
            moveRobotAlongPath(robot, pathToShelf, "shelf with product " + productId);

            // Simulate picking up the product
            log.info("Robot {} picking up {} units of product {} from shelf at ({}, {})",
                    robot.getSerialNumber(), quantityNeeded, productId,
                    targetShelf.getLocationX(), targetShelf.getLocationY());

            // Simulate pickup time based on quantity
            try {
                Thread.sleep(quantityNeeded * 200); // 200ms per item
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        // Calculate path back to base after collecting all products
        List<Point> pathToBase = findSimplePath(
                robot.getLocationX(), robot.getLocationY(),
                robot.getBaseX(), robot.getBaseY()  // Use robot's actual base coordinates
        );

        // Move robot back to base
        moveRobotAlongPath(robot, pathToBase, "base");

        log.info("Robot {} completed job for order {} - collected all {} products",
                robot.getSerialNumber(), order.getId(), order.getItems().size());
    }

    private Shelf findShelfWithProduct(Long productId) {
        return shelfRepository.findAll().stream()
                .filter(shelf -> shelf.getProduct().getId() == productId)
                .findFirst()
                .orElse(null);
    }

    /**
     * Simple pathfinding: Move horizontally first, then vertically
     * This avoids obstacles by checking each step
     */
    private List<Point> findSimplePath(int startX, int startY, int targetX, int targetY) {
        List<Point> path = new ArrayList<>();

        log.info("Calculating path from ({}, {}) to ({}, {})", startX, startY, targetX, targetY);

        // If already at target, no movement needed
        if (startX == targetX && startY == targetY) {
            log.info("Robot already at target position - no movement needed");
            return path;
        }

        int currentX = startX;
        int currentY = startY;

        // Move horizontally first
        while (currentX != targetX) {
            int nextX = currentX + (targetX > currentX ? 1 : -1);

            // Check if we can move to the next position
            if (warehouse.canMoveRobotTo(nextX, currentY)) {
                currentX = nextX;
                path.add(new Point(currentX, currentY));
                log.debug("Added horizontal step to ({}, {})", currentX, currentY);
            } else {
                log.warn("Horizontal movement blocked at ({}, {}), switching to vertical", nextX, currentY);
                break;
            }
        }

        // Move vertically
        while (currentY != targetY) {
            int nextY = currentY + (targetY > currentY ? 1 : -1);

            if (warehouse.canMoveRobotTo(currentX, nextY)) {
                currentY = nextY;
                path.add(new Point(currentX, currentY));
                log.debug("Added vertical step to ({}, {})", currentX, currentY);
            } else {
                log.warn("Vertical movement blocked at ({}, {}), trying alternative route", currentX, nextY);
                break;
            }
        }

        // Finish horizontal movement if not completed
        while (currentX != targetX) {
            int nextX = currentX + (targetX > currentX ? 1 : -1);

            if (warehouse.canMoveRobotTo(nextX, currentY)) {
                currentX = nextX;
                path.add(new Point(currentX, currentY));
                log.debug("Added final horizontal step to ({}, {})", currentX, currentY);
            } else {
                log.error("Cannot reach target ({}, {}) from ({}, {}) - path completely blocked",
                        targetX, targetY, currentX, currentY);
                break;
            }
        }

        // Check if we successfully reached the target
        if (currentX == targetX && currentY == targetY) {
            log.info("Path found successfully with {} steps", path.size());
        } else {
            log.error("Path incomplete - stopped at ({}, {}) instead of ({}, {})",
                    currentX, currentY, targetX, targetY);
        }

        return path;
    }

    private void moveRobotAlongPath(Robot robot, List<Point> path, String destination) {
        log.info("Robot {} moving to {} via {} steps", robot.getSerialNumber(), destination, path.size());

        for (int i = 0; i < path.size(); i++) {
            Point point = path.get(i);

            // Log current position before moving
            log.info("Robot {} at ({}, {}) -> moving to ({}, {}) [step {}/{}]",
                    robot.getSerialNumber(),
                    robot.getLocationX(), robot.getLocationY(),
                    point.x, point.y,
                    i + 1, path.size());

            // Update robot position
            robot.setLocationX(point.x);
            robot.setLocationY(point.y);

            log.info("Robot {} reached ({}, {})", robot.getSerialNumber(), point.x, point.y);

            // Simulate movement time
            try {
                Thread.sleep(500); // 0.5 seconds per step
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        log.info("Robot {} arrived at {} after {} steps", robot.getSerialNumber(), destination, path.size());
    }

    // Simple Point class for coordinates
    private static class Point {
        final int x;
        final int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}