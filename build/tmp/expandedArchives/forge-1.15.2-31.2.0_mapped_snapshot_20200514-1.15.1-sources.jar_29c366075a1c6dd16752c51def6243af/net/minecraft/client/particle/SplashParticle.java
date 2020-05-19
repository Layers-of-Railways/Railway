package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SplashParticle extends RainParticle {
   private SplashParticle(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn);
      this.particleGravity = 0.04F;
      if (ySpeedIn == 0.0D && (xSpeedIn != 0.0D || zSpeedIn != 0.0D)) {
         this.motionX = xSpeedIn;
         this.motionY = 0.1D;
         this.motionZ = zSpeedIn;
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public Factory(IAnimatedSprite p_i50679_1_) {
         this.spriteSet = p_i50679_1_;
      }

      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         SplashParticle splashparticle = new SplashParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
         splashparticle.selectSpriteRandomly(this.spriteSet);
         return splashparticle;
      }
   }
}