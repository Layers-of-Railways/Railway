package net.minecraft.state.properties;

import net.minecraft.util.IStringSerializable;

public enum PistonType implements IStringSerializable {
   DEFAULT("normal"),
   STICKY("sticky");

   private final String name;

   private PistonType(String name) {
      this.name = name;
   }

   public String toString() {
      return this.name;
   }

   public String getName() {
      return this.name;
   }
}