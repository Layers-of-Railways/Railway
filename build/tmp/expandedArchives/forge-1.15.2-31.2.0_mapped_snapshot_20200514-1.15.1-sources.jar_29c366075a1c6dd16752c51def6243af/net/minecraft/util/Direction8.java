package net.minecraft.util;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Set;

public enum Direction8 {
   NORTH(Direction.NORTH),
   NORTH_EAST(Direction.NORTH, Direction.EAST),
   EAST(Direction.EAST),
   SOUTH_EAST(Direction.SOUTH, Direction.EAST),
   SOUTH(Direction.SOUTH),
   SOUTH_WEST(Direction.SOUTH, Direction.WEST),
   WEST(Direction.WEST),
   NORTH_WEST(Direction.NORTH, Direction.WEST);

   private static final int field_208500_i = 1 << NORTH_WEST.ordinal();
   private static final int field_208501_j = 1 << WEST.ordinal();
   private static final int field_208502_k = 1 << SOUTH_WEST.ordinal();
   private static final int field_208503_l = 1 << SOUTH.ordinal();
   private static final int field_208504_m = 1 << SOUTH_EAST.ordinal();
   private static final int field_208505_n = 1 << EAST.ordinal();
   private static final int field_208506_o = 1 << NORTH_EAST.ordinal();
   private static final int field_208507_p = 1 << NORTH.ordinal();
   private final Set<Direction> directions;

   private Direction8(Direction... directionsIn) {
      this.directions = Sets.immutableEnumSet(Arrays.asList(directionsIn));
   }

   public Set<Direction> getDirections() {
      return this.directions;
   }
}