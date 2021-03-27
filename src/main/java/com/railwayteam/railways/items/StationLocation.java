package com.railwayteam.railways.items;

import net.minecraft.util.math.BlockPos;

public class StationLocation {
  public BlockPos coords;
  public String name;
  public StationLocation(BlockPos position) {
    coords = position;
    name = printCoords();
  }
  public StationLocation(BlockPos position, String name) {
    coords = position;
    this.name = name;
  }
  public StationLocation (String coordString) {
    String[] tokens = coordString.replace("(","").replace(")","").split(",");
    coords = new BlockPos(Integer.parseInt(tokens[0]),Integer.parseInt(tokens[1]),Integer.parseInt(tokens[2]));
    name = printCoords();
  }
  public boolean isAt (BlockPos position) { return coords.equals(position); }
  public String printCoords () { return String.format("(%d,%d,%d)", coords.getX(), coords.getY(), coords.getZ()); }
}
