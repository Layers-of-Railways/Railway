package net.minecraft.state.properties;

import net.minecraft.util.IStringSerializable;

public enum BellAttachment implements IStringSerializable {
   FLOOR("floor"),
   CEILING("ceiling"),
   SINGLE_WALL("single_wall"),
   DOUBLE_WALL("double_wall");

   private final String name;

   private BellAttachment(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }
}