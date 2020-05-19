package net.minecraft.state.properties;

import net.minecraft.util.IStringSerializable;

public enum Half implements IStringSerializable {
   TOP("top"),
   BOTTOM("bottom");

   private final String name;

   private Half(String name) {
      this.name = name;
   }

   public String toString() {
      return this.name;
   }

   public String getName() {
      return this.name;
   }
}