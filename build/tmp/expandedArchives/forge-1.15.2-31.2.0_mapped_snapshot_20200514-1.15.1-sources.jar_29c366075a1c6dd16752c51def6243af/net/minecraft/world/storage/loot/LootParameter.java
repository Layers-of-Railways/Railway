package net.minecraft.world.storage.loot;

import net.minecraft.util.ResourceLocation;

public class LootParameter<T> {
   private final ResourceLocation id;

   public LootParameter(ResourceLocation idIn) {
      this.id = idIn;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public String toString() {
      return "<parameter " + this.id + ">";
   }
}