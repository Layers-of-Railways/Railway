package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LavaParticle extends SpriteTexturedParticle {
   private LavaParticle(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0D, 0.0D, 0.0D);
      this.motionX *= (double)0.8F;
      this.motionY *= (double)0.8F;
      this.motionZ *= (double)0.8F;
      this.motionY = (double)(this.rand.nextFloat() * 0.4F + 0.05F);
      this.particleScale *= this.rand.nextFloat() * 2.0F + 0.2F;
      this.maxAge = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public int getBrightnessForRender(float partialTick) {
      int i = super.getBrightnessForRender(partialTick);
      int j = 240;
      int k = i >> 16 & 255;
      return 240 | k << 16;
   }

   public float getScale(float scaleFactor) {
      float f = ((float)this.age + scaleFactor) / (float)this.maxAge;
      return this.particleScale * (1.0F - f * f);
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      float f = (float)this.age / (float)this.maxAge;
      if (this.rand.nextFloat() > f) {
         this.world.addParticle(ParticleTypes.SMOKE, this.posX, this.posY, this.posZ, this.motionX, this.motionY, this.motionZ);
      }

      if (this.age++ >= this.maxAge) {
         this.setExpired();
      } else {
         this.motionY -= 0.03D;
         this.move(this.motionX, this.motionY, this.motionZ);
         this.motionX *= (double)0.999F;
         this.motionY *= (double)0.999F;
         this.motionZ *= (double)0.999F;
         if (this.onGround) {
            this.motionX *= (double)0.7F;
            this.motionZ *= (double)0.7F;
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public Factory(IAnimatedSprite p_i50495_1_) {
         this.spriteSet = p_i50495_1_;
      }

      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         LavaParticle lavaparticle = new LavaParticle(worldIn, x, y, z);
         lavaparticle.selectSpriteRandomly(this.spriteSet);
         return lavaparticle;
      }
   }
}