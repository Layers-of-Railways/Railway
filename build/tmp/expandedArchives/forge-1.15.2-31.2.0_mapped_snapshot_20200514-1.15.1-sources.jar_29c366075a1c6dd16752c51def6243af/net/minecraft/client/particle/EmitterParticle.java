package net.minecraft.client.particle;

import net.minecraft.entity.Entity;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EmitterParticle extends MetaParticle {
   private final Entity attachedEntity;
   private int age;
   private final int lifetime;
   private final IParticleData particleTypes;

   public EmitterParticle(World p_i47638_1_, Entity p_i47638_2_, IParticleData p_i47638_3_) {
      this(p_i47638_1_, p_i47638_2_, p_i47638_3_, 3);
   }

   public EmitterParticle(World p_i47639_1_, Entity p_i47639_2_, IParticleData p_i47639_3_, int p_i47639_4_) {
      this(p_i47639_1_, p_i47639_2_, p_i47639_3_, p_i47639_4_, p_i47639_2_.getMotion());
   }

   private EmitterParticle(World p_i50995_1_, Entity p_i50995_2_, IParticleData p_i50995_3_, int p_i50995_4_, Vec3d p_i50995_5_) {
      super(p_i50995_1_, p_i50995_2_.getPosX(), p_i50995_2_.getPosYHeight(0.5D), p_i50995_2_.getPosZ(), p_i50995_5_.x, p_i50995_5_.y, p_i50995_5_.z);
      this.attachedEntity = p_i50995_2_;
      this.lifetime = p_i50995_4_;
      this.particleTypes = p_i50995_3_;
      this.tick();
   }

   public void tick() {
      for(int i = 0; i < 16; ++i) {
         double d0 = (double)(this.rand.nextFloat() * 2.0F - 1.0F);
         double d1 = (double)(this.rand.nextFloat() * 2.0F - 1.0F);
         double d2 = (double)(this.rand.nextFloat() * 2.0F - 1.0F);
         if (!(d0 * d0 + d1 * d1 + d2 * d2 > 1.0D)) {
            double d3 = this.attachedEntity.getPosXWidth(d0 / 4.0D);
            double d4 = this.attachedEntity.getPosYHeight(0.5D + d1 / 4.0D);
            double d5 = this.attachedEntity.getPosZWidth(d2 / 4.0D);
            this.world.addParticle(this.particleTypes, false, d3, d4, d5, d0, d1 + 0.2D, d2);
         }
      }

      ++this.age;
      if (this.age >= this.lifetime) {
         this.setExpired();
      }

   }
}