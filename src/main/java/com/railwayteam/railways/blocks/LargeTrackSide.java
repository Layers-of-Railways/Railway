package com.railwayteam.railways.blocks;

import com.railwayteam.railways.Util;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.Vec3i;

import javax.annotation.Nonnull;

public enum LargeTrackSide implements IStringSerializable {
  NORTH_SOUTHWEST(    Util.Vector.NORTH,     Util.Vector.SOUTHWEST),
  NORTH_SOUTH(        Util.Vector.NORTH,     Util.Vector.SOUTH),
  NORTH_SOUTHEAST(    Util.Vector.NORTH,     Util.Vector.SOUTHEAST),
  EAST_NORTHWEST(     Util.Vector.EAST,      Util.Vector.NORTHWEST),
  EAST_WEST(          Util.Vector.EAST,      Util.Vector.WEST),
  EAST_SOUTHWEST(     Util.Vector.EAST,      Util.Vector.SOUTHWEST),
  SOUTH_NORTHEAST(    Util.Vector.SOUTH,     Util.Vector.NORTHEAST),
  SOUTH_NORTHWEST(    Util.Vector.SOUTH,     Util.Vector.NORTHWEST),
  WEST_SOUTHEAST(     Util.Vector.WEST,      Util.Vector.SOUTHEAST),
  WEST_NORTHEAST(     Util.Vector.WEST,      Util.Vector.NORTHEAST),
  NORTHEAST_SOUTHWEST(Util.Vector.NORTHEAST, Util.Vector.SOUTHWEST),
  NORTHWEST_SOUTHEAST(Util.Vector.NORTHWEST, Util.Vector.SOUTHEAST);

  private final String name;
  private final Vec3i[] offsets;
  private LargeTrackSide (Util.Vector dir1, Util.Vector dir2) {
    this.name = dir1.name + "_" + dir2.name;
    offsets = new Vec3i[2];
    offsets[0] = dir1.value;
    offsets[1] = dir2.value;
  }

  public String toString() { return this.name; }

  @Override
  @Nonnull
  public String getName() {
    return this.name;
  }

  public boolean isCardinal () {
    return this == NORTH_SOUTH || this == EAST_WEST;
  }
  public boolean isDiagonal () {
    return this == NORTHEAST_SOUTHWEST || this == NORTHWEST_SOUTHEAST;
  }
  public boolean isStraight () {
    return this.isCardinal() || this.isDiagonal();
  }

  public boolean connectsTo (Vec3i offset) {
    return offsets[0].equals(offset) || offsets[1].equals(offset);
  }

  public static boolean isValid    (Vec3i a, Vec3i b) {
    for (LargeTrackSide side : values()) {
      if (side.offsets[0].equals(a) && side.offsets[1].equals(b)
      ||  side.offsets[1].equals(a) && side.offsets[0].equals(b)
      ) {
        return true;
      }
    }
    return false;
  }

  public static LargeTrackSide findValidStateFrom(Vec3i a) {
    // try to find a straight track
    for (LargeTrackSide side : values()) {
      if (!side.isStraight()) continue;
      if (side.offsets[0].equals(a) || side.offsets[1].equals(a)) return side;
    }
    return NORTH_SOUTH;
  }

  public static LargeTrackSide findValidStateFrom(Vec3i a, Vec3i b) {
    for (LargeTrackSide side : LargeTrackSide.values()) {
      if (!side.connectsTo(a)) continue;
      if (!side.connectsTo(b)) continue;
      return side; // found it
    }
    return NORTH_SOUTH;
  }
}
