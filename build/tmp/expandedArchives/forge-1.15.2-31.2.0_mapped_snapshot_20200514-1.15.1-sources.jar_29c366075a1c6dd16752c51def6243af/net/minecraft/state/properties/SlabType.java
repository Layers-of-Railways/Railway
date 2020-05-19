package net.minecraft.state.properties;

import net.minecraft.util.IStringSerializable;

public enum SlabType implements IStringSerializable {
   TOP("top"),
   BOTTOM("bottom"),
   DOUBLE("double");

   private final String name;

   private SlabType(String name) {
      this.name = name;
   }

   public String toString() {
      return this.name;
   }

   public String getName() {
      return this.name;
   }
}