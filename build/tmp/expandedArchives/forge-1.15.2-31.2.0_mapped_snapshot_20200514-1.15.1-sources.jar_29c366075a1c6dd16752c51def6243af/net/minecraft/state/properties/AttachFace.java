package net.minecraft.state.properties;

import net.minecraft.util.IStringSerializable;

public enum AttachFace implements IStringSerializable {
   FLOOR("floor"),
   WALL("wall"),
   CEILING("ceiling");

   private final String name;

   private AttachFace(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }
}