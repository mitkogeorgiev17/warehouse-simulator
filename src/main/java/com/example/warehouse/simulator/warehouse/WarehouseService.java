package com.example.warehouse.simulator.warehouse;

import com.example.warehouse.simulator.robot.RobotRepository;
import com.example.warehouse.simulator.shelf.ShelfRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    public boolean canPlaceShelfAt(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height &&
                !shelfRepository.existsByLocationXAndLocationY(x, y);
    }

    public boolean canMoveRobotTo(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height &&
                !shelfRepository.existsByLocationXAndLocationY(x, y);
    }
}
