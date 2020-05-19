package net.minecraft.util;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SoundEvent extends net.minecraftforge.registries.ForgeRegistryEntry<SoundEvent> {
   private final ResourceLocation name;

   public SoundEvent(ResourceLocation name) {
      this.name = name;
   }

   @OnlyIn(Dist.CLIENT)
   public ResourceLocation getName() {
      return this.name;
   }
}