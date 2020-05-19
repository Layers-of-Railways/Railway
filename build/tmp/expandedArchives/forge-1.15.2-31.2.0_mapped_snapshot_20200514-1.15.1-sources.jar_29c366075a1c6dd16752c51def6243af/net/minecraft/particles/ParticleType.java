package net.minecraft.particles;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ParticleType<T extends IParticleData> extends net.minecraftforge.registries.ForgeRegistryEntry<ParticleType<?>> {
   private final boolean alwaysShow;
   private final IParticleData.IDeserializer<T> deserializer;

   public ParticleType(boolean p_i50792_1_, IParticleData.IDeserializer<T> p_i50792_2_) {
      this.alwaysShow = p_i50792_1_;
      this.deserializer = p_i50792_2_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean getAlwaysShow() {
      return this.alwaysShow;
   }

   public IParticleData.IDeserializer<T> getDeserializer() {
      return this.deserializer;
   }
}