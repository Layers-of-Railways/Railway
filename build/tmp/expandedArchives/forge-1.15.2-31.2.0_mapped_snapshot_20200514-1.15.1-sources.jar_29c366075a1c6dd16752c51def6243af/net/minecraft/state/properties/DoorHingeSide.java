package net.minecraft.state.properties;

import net.minecraft.util.IStringSerializable;

public enum DoorHingeSide implements IStringSerializable {
   LEFT,
   RIGHT;

   public String toString() {
      return this.getName();
   }

   public String getName() {
      return this == LEFT ? "left" : "right";
   }
}