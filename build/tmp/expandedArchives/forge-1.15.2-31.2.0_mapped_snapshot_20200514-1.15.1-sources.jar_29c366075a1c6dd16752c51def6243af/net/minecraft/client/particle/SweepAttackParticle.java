package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SweepAttackParticle extends SpriteTexturedParticle {
   private final IAnimatedSprite field_217570_C;

   private SweepAttackParticle(World p_i51054_1_, double p_i51054_2_, double p_i51054_4_, double p_i51054_6_, double p_i51054_8_, IAnimatedSprite p_i51054_10_) {
      super(p_i51054_1_, p_i51054_2_, p_i51054_4_, p_i51054_6_, 0.0D, 0.0D, 0.0D);
      this.field_217570_C = p_i51054_10_;
      this.maxAge = 4;
      float f = this.rand.nextFloat() * 0.6F + 0.4F;
      this.particleRed = f;
      this.particleGreen = f;
      this.particleBlue = f;
      this.particleScale = 1.0F - (float)p_i51054_8_ * 0.5F;
      this.selectSpriteWithAge(p_i51054_10_);
   }

   public int getBrightnessForRender(float partialTick) {
      return 15728880;
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.age++ >= this.maxAge) {
         this.setExpired();
      } else {
         this.selectSpriteWithAge(this.field_217570_C);
      }
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.PARTICLE_SHEET_LIT;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public Factory(IAnimatedSprite p_i50563_1_) {
         this.spriteSet = p_i50563_1_;
      }

      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new SweepAttackParticle(worldIn, x, y, z, xSpeed, this.spriteSet);
      }
   }
}