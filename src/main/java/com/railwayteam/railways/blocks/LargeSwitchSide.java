package com.railwayteam.railways.blocks;

import com.railwayteam.railways.Util;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.vector.Vector3i;

import javax.annotation.Nonnull;

public enum LargeSwitchSide implements IStringSerializable {
  NORTH_SOUTHEAST (Util.Vector.NORTH, Util.Vector.SOUTHEAST, "n_se"),
  NORTH_SOUTHWEST (Util.Vector.NORTH, Util.Vector.SOUTHWEST, "n_sw"),
  SOUTH_NORTHEAST (Util.Vector.SOUTH, Util.Vector.NORTHEAST, "s_ne"),
  SOUTH_NORTHWEST (Util.Vector.SOUTH, Util.Vector.NORTHWEST, "s_nw"),
  EAST_NORTHWEST  (Util.Vector.EAST,  Util.Vector.NORTHWEST, "e_nw"),
  EAST_SOUTHWEST  (Util.Vector.EAST,  Util.Vector.SOUTHWEST, "e_sw"),
  WEST_NORTHEAST  (Util.Vector.WEST,  Util.Vector.NORTHEAST, "w_ne"),
  WEST_SOUTHEAST  (Util.Vector.WEST,  Util.Vector.SOUTHEAST, "w_se");

  private final String name;
  private final Vector3i[] offsets;
  private LargeSwitchSide (Util.Vector axis, Util.Vector turn, String name) {
    this.name = name;
    offsets = new Vector3i[3];
    offsets[0] = axis.value;
    offsets[1] = axis.getOpposite().value;
    offsets[2] = turn.value;
  }

  public String toString() { return this.name; }

  @Override
  @Nonnull
  public String getSerializedName() {
    return this.name;
  }

  public boolean connectsTo (Vector3i offset) {
    return offsets[0].equals(offset) || offsets[1].equals(offset) || offsets[2].equals(offset);
  }

  public static LargeSwitchSide findValidStateFrom(Vector3i a) {
    for (LargeSwitchSide side : values()) {
      if (side.offsets[0].equals(a) || side.offsets[1].equals(a)) return side;
      if (side.offsets[2].equals(a)) return side; // else, just pick one using the "turn" side
    }
    return NORTH_SOUTHEAST;
  }

  public static LargeSwitchSide findValidStateFrom(Vector3i a, Vector3i b) {
    for (LargeSwitchSide side : LargeSwitchSide.values()) {
      if (!side.connectsTo(a)) continue;
      if (!side.connectsTo(b)) continue;
      return side; // found it
    }
    return NORTH_SOUTHEAST;
  }

  public static LargeSwitchSide findValidStateFrom (Vector3i a, Vector3i b, Vector3i c) {
    for (LargeSwitchSide side : LargeSwitchSide.values()) {
      if (side.connectsTo(a) && side.connectsTo(b) && side.connectsTo(c)) return side;
    }
    return NORTH_SOUTHEAST;
  }
}
