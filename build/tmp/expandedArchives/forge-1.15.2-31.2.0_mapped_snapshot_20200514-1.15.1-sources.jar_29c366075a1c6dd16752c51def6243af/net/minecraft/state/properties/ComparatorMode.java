package net.minecraft.state.properties;

import net.minecraft.util.IStringSerializable;

public enum ComparatorMode implements IStringSerializable {
   COMPARE("compare"),
   SUBTRACT("subtract");

   private final String name;

   private ComparatorMode(String name) {
      this.name = name;
   }

   public String toString() {
      return this.name;
   }

   public String getName() {
      return this.name;
   }
}