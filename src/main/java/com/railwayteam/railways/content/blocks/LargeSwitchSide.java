package com.railwayteam.railways.content.blocks;

import com.railwayteam.railways.util.VectorUtils;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;

public enum LargeSwitchSide implements IStringSerializable {
  NORTH_SOUTHEAST (VectorUtils.Vector.NORTH, VectorUtils.Vector.SOUTHEAST, "n_se"),
  NORTH_SOUTHWEST (VectorUtils.Vector.NORTH, VectorUtils.Vector.SOUTHWEST, "n_sw"),
  SOUTH_NORTHEAST (VectorUtils.Vector.SOUTH, VectorUtils.Vector.NORTHEAST, "s_ne"),
  SOUTH_NORTHWEST (VectorUtils.Vector.SOUTH, VectorUtils.Vector.NORTHWEST, "s_nw"),
  EAST_NORTHWEST  (VectorUtils.Vector.EAST,  VectorUtils.Vector.NORTHWEST, "e_nw"),
  EAST_SOUTHWEST  (VectorUtils.Vector.EAST,  VectorUtils.Vector.SOUTHWEST, "e_sw"),
  WEST_NORTHEAST  (VectorUtils.Vector.WEST,  VectorUtils.Vector.NORTHEAST, "w_ne"),
  WEST_SOUTHEAST  (VectorUtils.Vector.WEST,  VectorUtils.Vector.SOUTHEAST, "w_se");

  private final String name;
  private final BlockPos[] offsets;
  LargeSwitchSide(VectorUtils.Vector axis, VectorUtils.Vector turn, String name) {
    this.name = name;
    offsets = new BlockPos[3];
    offsets[0] = axis.value;
    offsets[1] = axis.getOpposite().value;
    offsets[2] = turn.value;
  }

  public String toString() { return this.name; }

  public boolean connectsTo (BlockPos offset) {
    return offsets[0].equals(offset) || offsets[1].equals(offset) || offsets[2].equals(offset);
  }

  public static LargeSwitchSide findValidStateFrom(BlockPos a) {
    for (LargeSwitchSide side : values()) {
      if (side.offsets[0].equals(a) || side.offsets[1].equals(a)) return side;
      if (side.offsets[2].equals(a)) return side; // else, just pick one using the "turn" side
    }
    return null; //NORTH_SOUTHEAST
  }

  public static LargeSwitchSide findValidStateFrom(BlockPos a, BlockPos b) {
    for (LargeSwitchSide side : LargeSwitchSide.values()) {
      if (!side.connectsTo(a)) continue;
      if (!side.connectsTo(b)) continue;
      return side; // found it
    }
    return null; // NORTH_SOUTHEAST
  }

  public static LargeSwitchSide findValidStateFrom (BlockPos a, BlockPos b, BlockPos c) {
    for (LargeSwitchSide side : LargeSwitchSide.values()) {
      if (side.connectsTo(a) && side.connectsTo(b) && side.connectsTo(c)) return side;
    }
    return null; // NORTH_SOUTHEAST
  }

  @Override
  public String getString() {
    return this.name;
  }
}