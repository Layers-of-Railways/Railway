package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HeartParticle extends SpriteTexturedParticle {
   private HeartParticle(World p_i51030_1_, double p_i51030_2_, double p_i51030_4_, double p_i51030_6_) {
      super(p_i51030_1_, p_i51030_2_, p_i51030_4_, p_i51030_6_, 0.0D, 0.0D, 0.0D);
      this.motionX *= (double)0.01F;
      this.motionY *= (double)0.01F;
      this.motionZ *= (double)0.01F;
      this.motionY += 0.1D;
      this.particleScale *= 1.5F;
      this.maxAge = 16;
      this.canCollide = false;
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public float getScale(float scaleFactor) {
      return this.particleScale * MathHelper.clamp(((float)this.age + scaleFactor) / (float)this.maxAge * 32.0F, 0.0F, 1.0F);
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.age++ >= this.maxAge) {
         this.setExpired();
      } else {
         this.move(this.motionX, this.motionY, this.motionZ);
         if (this.posY == this.prevPosY) {
            this.motionX *= 1.1D;
            this.motionZ *= 1.1D;
         }

         this.motionX *= (double)0.86F;
         this.motionY *= (double)0.86F;
         this.motionZ *= (double)0.86F;
         if (this.onGround) {
            this.motionX *= (double)0.7F;
            this.motionZ *= (double)0.7F;
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class AngryVillagerFactory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public AngryVillagerFactory(IAnimatedSprite p_i50748_1_) {
         this.spriteSet = p_i50748_1_;
      }

      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         HeartParticle heartparticle = new HeartParticle(worldIn, x, y + 0.5D, z);
         heartparticle.selectSpriteRandomly(this.spriteSet);
         heartparticle.setColor(1.0F, 1.0F, 1.0F);
         return heartparticle;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public Factory(IAnimatedSprite p_i50747_1_) {
         this.spriteSet = p_i50747_1_;
      }

      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         HeartParticle heartparticle = new HeartParticle(worldIn, x, y, z);
         heartparticle.selectSpriteRandomly(this.spriteSet);
         return heartparticle;
      }
   }
}