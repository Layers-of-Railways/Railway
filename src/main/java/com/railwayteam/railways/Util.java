package com.railwayteam.railways;

import net.minecraft.util.math.Vec3i;

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

    public Vec3i value;
    public String name;

    private Vector(int x, int y, int z, String name) {
      value = new Vec3i(x, y, z);
      this.name = name;
    }

    public static Vector getClosest (Vec3i candidate) {
      for (Vector v : values()) {
        if (Integer.signum(candidate.getX()) != v.value.getX()) continue;
        if (Integer.signum(candidate.getZ()) != v.value.getZ()) continue;
        return v;
      }
      return SOUTH;
    }

    public Vector getOpposite () {
      return getOpposite(this);
    }

    public static Vector getOpposite (Vector in) {
      switch (in) {
        case NORTH: return SOUTH;
        case SOUTH: return NORTH;
        case EAST:  return WEST;
        case WEST:  return EAST;
        case NORTHWEST: return SOUTHEAST;
        case NORTHEAST: return SOUTHWEST;
        case SOUTHWEST: return NORTHEAST;
        case SOUTHEAST: return NORTHWEST;
      }
      return SOUTH; // should never get here
    }
  }

  public static Vec3i opposite (Vec3i in) {
    return new Vec3i (in.getX()*-1, in.getY()*-1, in.getZ()*-1);
  }
}
