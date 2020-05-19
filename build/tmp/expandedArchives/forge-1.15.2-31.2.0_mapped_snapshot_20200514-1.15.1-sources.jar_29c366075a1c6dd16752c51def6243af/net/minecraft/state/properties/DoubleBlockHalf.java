package net.minecraft.state.properties;

import net.minecraft.util.IStringSerializable;

public enum DoubleBlockHalf implements IStringSerializable {
   UPPER,
   LOWER;

   public String toString() {
      return this.getName();
   }

   public String getName() {
      return this == UPPER ? "upper" : "lower";
   }
}