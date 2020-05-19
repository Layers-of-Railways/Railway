package net.minecraft.state.properties;

import net.minecraft.util.IStringSerializable;

public enum BedPart implements IStringSerializable {
   HEAD("head"),
   FOOT("foot");

   private final String name;

   private BedPart(String name) {
      this.name = name;
   }

   public String toString() {
      return this.name;
   }

   public String getName() {
      return this.name;
   }
}