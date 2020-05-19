package net.minecraft.state.properties;

import net.minecraft.util.IStringSerializable;

public enum RailShape implements IStringSerializable {
   NORTH_SOUTH("north_south"),
   EAST_WEST("east_west"),
   ASCENDING_EAST("ascending_east"),
   ASCENDING_WEST("ascending_west"),
   ASCENDING_NORTH("ascending_north"),
   ASCENDING_SOUTH("ascending_south"),
   SOUTH_EAST("south_east"),
   SOUTH_WEST("south_west"),
   NORTH_WEST("north_west"),
   NORTH_EAST("north_east");

   private final String name;

   private RailShape(String p_i225774_3_) {
      this.name = p_i225774_3_;
   }

   public String toString() {
      return this.name;
   }

   public boolean isAscending() {
      return this == ASCENDING_NORTH || this == ASCENDING_EAST || this == ASCENDING_SOUTH || this == ASCENDING_WEST;
   }

   public String getName() {
      return this.name;
   }
}