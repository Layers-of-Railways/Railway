package com.railwayteam.railways;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

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

    public Vector3d value;
    public String name;

    private Vector(int x, int y, int z, String name) {
      value = new Vector3d(x, y, z);
      this.name = name;
    }

    public static Vector getClosest (BlockPos candidate) {
      return getClosest(new Vector3d(
        Math.signum(Math.round(candidate.getX())),
        0,
        Math.signum(Math.round(candidate.getZ()))
      ));
    }

    public static Vector getClosest (Vector3d candidate) {
      for (Vector v : values()) {
        if (Integer.signum((int) candidate.getX()) != v.value.getX()) continue;
        if (Integer.signum((int) candidate.getZ()) != v.value.getZ()) continue;
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

  public static BlockPos opposite (BlockPos in) {
    return new BlockPos (in.getX()*-1, in.getY()*-1, in.getZ()*-1);
  }

  public static Vector3d opposite(Vector3d in) {
    return new Vector3d (in.getX()*-1, in.getY()*-1, in.getZ()*-1);
  }
}
