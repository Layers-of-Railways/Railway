package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TotemOfUndyingParticle extends SimpleAnimatedParticle {
   private TotemOfUndyingParticle(World p_i50996_1_, double p_i50996_2_, double p_i50996_4_, double p_i50996_6_, double p_i50996_8_, double p_i50996_10_, double p_i50996_12_, IAnimatedSprite p_i50996_14_) {
      super(p_i50996_1_, p_i50996_2_, p_i50996_4_, p_i50996_6_, p_i50996_14_, -0.05F);
      this.motionX = p_i50996_8_;
      this.motionY = p_i50996_10_;
      this.motionZ = p_i50996_12_;
      this.particleScale *= 0.75F;
      this.maxAge = 60 + this.rand.nextInt(12);
      this.selectSpriteWithAge(p_i50996_14_);
      if (this.rand.nextInt(4) == 0) {
         this.setColor(0.6F + this.rand.nextFloat() * 0.2F, 0.6F + this.rand.nextFloat() * 0.3F, this.rand.nextFloat() * 0.2F);
      } else {
         this.setColor(0.1F + this.rand.nextFloat() * 0.2F, 0.4F + this.rand.nextFloat() * 0.3F, this.rand.nextFloat() * 0.2F);
      }

      this.setBaseAirFriction(0.6F);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public Factory(IAnimatedSprite p_i50316_1_) {
         this.spriteSet = p_i50316_1_;
      }

      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new TotemOfUndyingParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
      }
   }
}