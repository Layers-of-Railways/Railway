package com.railwayteam.railways;

import net.minecraft.util.math.Vec3d;

public class Util {
  public enum Vector {
    NORTH    ( 0, 0, -1, "n"),
    SOUTH    ( 0, 0,  1, "s"),
    EAST     ( 1, 0,  0, "e"),
    WEST     (-1, 0,  0, "w"),
    NORTHWEST(-1, 0, -1, "nw"),
    NORTHEAST( 1, 0, -1, "ne"),
    SOUTHWEST(-1, 0,  1, "sw"),
    SOUTHEAST( 1, 0,  1, "se");

    public Vec3d value;
    public String name;

    private Vector(int x, int y, int z, String name) {
      value = new Vec3d(x, y, z).normalize();
      this.name = name;
    }
  }
}
