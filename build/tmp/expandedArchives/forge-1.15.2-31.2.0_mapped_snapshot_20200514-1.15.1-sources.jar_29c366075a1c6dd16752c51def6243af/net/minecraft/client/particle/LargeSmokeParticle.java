package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LargeSmokeParticle extends SmokeParticle {
   protected LargeSmokeParticle(World p_i51024_1_, double p_i51024_2_, double p_i51024_4_, double p_i51024_6_, double p_i51024_8_, double p_i51024_10_, double p_i51024_12_, IAnimatedSprite p_i51024_14_) {
      super(p_i51024_1_, p_i51024_2_, p_i51024_4_, p_i51024_6_, p_i51024_8_, p_i51024_10_, p_i51024_12_, 2.5F, p_i51024_14_);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public Factory(IAnimatedSprite p_i50554_1_) {
         this.spriteSet = p_i50554_1_;
      }

      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new LargeSmokeParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
      }
   }
}