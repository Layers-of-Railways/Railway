package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DragonBreathParticle extends SpriteTexturedParticle {
   private boolean hasHitGround;
   private final IAnimatedSprite field_217574_F;

   private DragonBreathParticle(World p_i51042_1_, double p_i51042_2_, double p_i51042_4_, double p_i51042_6_, double p_i51042_8_, double p_i51042_10_, double p_i51042_12_, IAnimatedSprite p_i51042_14_) {
      super(p_i51042_1_, p_i51042_2_, p_i51042_4_, p_i51042_6_);
      this.motionX = p_i51042_8_;
      this.motionY = p_i51042_10_;
      this.motionZ = p_i51042_12_;
      this.particleRed = MathHelper.nextFloat(this.rand, 0.7176471F, 0.8745098F);
      this.particleGreen = MathHelper.nextFloat(this.rand, 0.0F, 0.0F);
      this.particleBlue = MathHelper.nextFloat(this.rand, 0.8235294F, 0.9764706F);
      this.particleScale *= 0.75F;
      this.maxAge = (int)(20.0D / ((double)this.rand.nextFloat() * 0.8D + 0.2D));
      this.hasHitGround = false;
      this.canCollide = false;
      this.field_217574_F = p_i51042_14_;
      this.selectSpriteWithAge(p_i51042_14_);
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.age++ >= this.maxAge) {
         this.setExpired();
      } else {
         this.selectSpriteWithAge(this.field_217574_F);
         if (this.onGround) {
            this.motionY = 0.0D;
            this.hasHitGround = true;
         }

         if (this.hasHitGround) {
            this.motionY += 0.002D;
         }

         this.move(this.motionX, this.motionY, this.motionZ);
         if (this.posY == this.prevPosY) {
            this.motionX *= 1.1D;
            this.motionZ *= 1.1D;
         }

         this.motionX *= (double)0.96F;
         this.motionZ *= (double)0.96F;
         if (this.hasHitGround) {
            this.motionY *= (double)0.96F;
         }

      }
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public float getScale(float scaleFactor) {
      return this.particleScale * MathHelper.clamp(((float)this.age + scaleFactor) / (float)this.maxAge * 32.0F, 0.0F, 1.0F);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public Factory(IAnimatedSprite p_i50559_1_) {
         this.spriteSet = p_i50559_1_;
      }

      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new DragonBreathParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
      }
   }
}