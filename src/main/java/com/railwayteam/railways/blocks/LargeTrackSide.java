package com.railwayteam.railways.blocks;

import com.railwayteam.railways.util.VectorUtils;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

public enum LargeTrackSide implements IStringSerializable {
  NORTH_SOUTHWEST(    VectorUtils.Vector.NORTH,     VectorUtils.Vector.SOUTHWEST),
  NORTH_SOUTH(        VectorUtils.Vector.NORTH,     VectorUtils.Vector.SOUTH),
  NORTH_SOUTHEAST(    VectorUtils.Vector.NORTH,     VectorUtils.Vector.SOUTHEAST),
  EAST_NORTHWEST(     VectorUtils.Vector.EAST,      VectorUtils.Vector.NORTHWEST),
  EAST_WEST(          VectorUtils.Vector.EAST,      VectorUtils.Vector.WEST),
  EAST_SOUTHWEST(     VectorUtils.Vector.EAST,      VectorUtils.Vector.SOUTHWEST),
  SOUTH_NORTHEAST(    VectorUtils.Vector.SOUTH,     VectorUtils.Vector.NORTHEAST),
  SOUTH_NORTHWEST(    VectorUtils.Vector.SOUTH,     VectorUtils.Vector.NORTHWEST),
  WEST_SOUTHEAST(     VectorUtils.Vector.WEST,      VectorUtils.Vector.SOUTHEAST),
  WEST_NORTHEAST(     VectorUtils.Vector.WEST,      VectorUtils.Vector.NORTHEAST),
  NORTHEAST_SOUTHWEST(VectorUtils.Vector.NORTHEAST, VectorUtils.Vector.SOUTHWEST),
  NORTHWEST_SOUTHEAST(VectorUtils.Vector.NORTHWEST, VectorUtils.Vector.SOUTHEAST);

  private final String name;
  private final BlockPos[] offsets;
  private LargeTrackSide (VectorUtils.Vector dir1, VectorUtils.Vector dir2) {
    this.name = dir1.name + "_" + dir2.name;
    offsets = new BlockPos[2];
    offsets[0] = dir1.value;
    offsets[1] = dir2.value;
  }

  public String toString() { return this.name; }

//  @Override
//  @Nonnull
//  public String getName() {
//    return this.name;
//  }

  public boolean isCardinal () {
    return this == NORTH_SOUTH || this == EAST_WEST;
  }
  public boolean isDiagonal () {
    return this == NORTHEAST_SOUTHWEST || this == NORTHWEST_SOUTHEAST;
  }
  public boolean isStraight () {
    return this.isCardinal() || this.isDiagonal();
  }

  public boolean connectsTo (BlockPos offset) {
    return offsets[0].equals(offset) || offsets[1].equals(offset);
  }

  public static boolean isValid    (BlockPos a, BlockPos b) {
    for (LargeTrackSide side : values()) {
      if (side.offsets[0].equals(a) && side.offsets[1].equals(b)
      ||  side.offsets[1].equals(a) && side.offsets[0].equals(b)
      ) {
        return true;
      }
    }
    return false;
  }

  public static LargeTrackSide findValidStateFrom(BlockPos a) {
    // try to find a straight track
    for (LargeTrackSide side : values()) {
      if (!side.isStraight()) continue;
      if (side.offsets[0].equals(a) || side.offsets[1].equals(a)) return side;
    }
    return NORTH_SOUTH;
  }

  public static LargeTrackSide findValidStateFrom(BlockPos a, BlockPos b) {
    for (LargeTrackSide side : LargeTrackSide.values()) {
      if (!side.connectsTo(a)) continue;
      if (!side.connectsTo(b)) continue;
      return side; // found it
    }
    return NORTH_SOUTH;
  }

  @Override
  public String getString() {
    return this.name;
  }
}
