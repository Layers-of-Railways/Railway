package net.minecraft.state.properties;

import net.minecraft.util.IStringSerializable;

public enum StructureMode implements IStringSerializable {
   SAVE("save"),
   LOAD("load"),
   CORNER("corner"),
   DATA("data");

   private final String name;

   private StructureMode(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }
}