package com.example.warehouse.simulator.warehouse.utils;

import java.util.List;

public class GridUtils {
    public static boolean isCellAvailableForPlacement(List<List<String>> grid, int x, int y) {
        if (grid == null || y < 0 || y >= grid.size() || x < 0 || x >= grid.get(0).size()) {
            return false;
        }
        return ".".equals(grid.get(y).get(x));
    }
}
