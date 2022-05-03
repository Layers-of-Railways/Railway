package com.railwayteam.railways.base;

import com.railwayteam.railways.Railways;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;

public class ConnectionHelper {
  public static final int MAX_SIZE = 5;

  Set<BlockPos> visited;
  ArrayList<BlockPos> frontier;

  public void search (Level level, HorizontalConnectedBlock type, BlockPos origin) {
    visited  = new HashSet<>();
    frontier = new ArrayList<>();
    frontier.add(origin);

    int minX = Integer.MAX_VALUE;
    int minY = Integer.MAX_VALUE;
    int minZ = Integer.MAX_VALUE;

    for (BlockPos pos : frontier) {
      minX = Math.min(minX, pos.getX());
      minY = Math.min(minY, pos.getY());
      minZ = Math.min(minZ, pos.getZ());
    }

    minX -= MAX_SIZE;
    minY -= MAX_SIZE;
    minZ -= MAX_SIZE;

    // search for neighbors
    while (!frontier.isEmpty()) {
      BlockPos current = frontier.remove(0);
      if (visited.contains(current)) continue;
      visited.add(current);

      for (Direction dir : Direction.values()) {
        BlockPos neighbor = current.relative(dir);

        if (!level.getBlockState(neighbor).getBlock().equals(type)) continue; // wrong block type
        if (visited.contains(neighbor)) continue; // we've been there already
        if (neighbor.getX() <= minX || neighbor.getY() <= minY || neighbor.getZ() <= minZ
        ||  neighbor.getX() >= minX + 2*MAX_SIZE || neighbor.getY() >= minY + 2*MAX_SIZE || neighbor.getZ() >= minZ + 2*MAX_SIZE
        ) continue; // beyond the bounds
        frontier.add(neighbor);
      }
    }

    int negX = origin.getX(), negZ = origin.getZ(), negY = origin.getY();
    int posX = origin.getX(), posZ = origin.getZ(), posY = origin.getY();
    for (BlockPos pos : visited) {
      negX = Math.min(negX, pos.getX());
      negZ = Math.min(negZ, pos.getZ());
      negY = Math.min(negY, pos.getY());

      posX = Math.max(posX, pos.getX());
      posZ = Math.max(posZ, pos.getZ());
      posY = Math.max(posY, pos.getY());
    }
    Railways.LOGGER.info("found " + visited.size() + " blocks, with size (" + (1+posX-negX) + "," + (1+posY-negY) + "," + (1+posZ-negZ) + ")");
  }
}
