package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpitParticle extends PoofParticle {
   private SpitParticle(World p_i51006_1_, double p_i51006_2_, double p_i51006_4_, double p_i51006_6_, double p_i51006_8_, double p_i51006_10_, double p_i51006_12_, IAnimatedSprite p_i51006_14_) {
      super(p_i51006_1_, p_i51006_2_, p_i51006_4_, p_i51006_6_, p_i51006_8_, p_i51006_10_, p_i51006_12_, p_i51006_14_);
      this.particleGravity = 0.5F;
   }

   public void tick() {
      super.tick();
      this.motionY -= 0.004D + 0.04D * (double)this.particleGravity;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public Factory(IAnimatedSprite p_i50812_1_) {
         this.spriteSet = p_i50812_1_;
      }

      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new SpitParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
      }
   }
}