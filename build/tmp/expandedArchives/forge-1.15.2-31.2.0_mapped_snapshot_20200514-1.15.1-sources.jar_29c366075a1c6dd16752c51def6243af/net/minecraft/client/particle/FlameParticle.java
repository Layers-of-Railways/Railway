package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FlameParticle extends SpriteTexturedParticle {
   private FlameParticle(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
      this.motionX = this.motionX * (double)0.01F + xSpeedIn;
      this.motionY = this.motionY * (double)0.01F + ySpeedIn;
      this.motionZ = this.motionZ * (double)0.01F + zSpeedIn;
      this.posX += (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.05F);
      this.posY += (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.05F);
      this.posZ += (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.05F);
      this.maxAge = (int)(8.0D / (Math.random() * 0.8D + 0.2D)) + 4;
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public void move(double x, double y, double z) {
      this.setBoundingBox(this.getBoundingBox().offset(x, y, z));
      this.resetPositionToBB();
   }

   public float getScale(float scaleFactor) {
      float f = ((float)this.age + scaleFactor) / (float)this.maxAge;
      return this.particleScale * (1.0F - f * f * 0.5F);
   }

   public int getBrightnessForRender(float partialTick) {
      float f = ((float)this.age + partialTick) / (float)this.maxAge;
      f = MathHelper.clamp(f, 0.0F, 1.0F);
      int i = super.getBrightnessForRender(partialTick);
      int j = i & 255;
      int k = i >> 16 & 255;
      j = j + (int)(f * 15.0F * 16.0F);
      if (j > 240) {
         j = 240;
      }

      return j | k << 16;
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.age++ >= this.maxAge) {
         this.setExpired();
      } else {
         this.move(this.motionX, this.motionY, this.motionZ);
         this.motionX *= (double)0.96F;
         this.motionY *= (double)0.96F;
         this.motionZ *= (double)0.96F;
         if (this.onGround) {
            this.motionX *= (double)0.7F;
            this.motionZ *= (double)0.7F;
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public Factory(IAnimatedSprite p_i50823_1_) {
         this.spriteSet = p_i50823_1_;
      }

      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         FlameParticle flameparticle = new FlameParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
         flameparticle.selectSpriteRandomly(this.spriteSet);
         return flameparticle;
      }
   }
}