package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BubblePopParticle extends SpriteTexturedParticle {
   private final IAnimatedSprite field_217573_C;

   private BubblePopParticle(World p_i51048_1_, double p_i51048_2_, double p_i51048_4_, double p_i51048_6_, double p_i51048_8_, double p_i51048_10_, double p_i51048_12_, IAnimatedSprite p_i51048_14_) {
      super(p_i51048_1_, p_i51048_2_, p_i51048_4_, p_i51048_6_);
      this.field_217573_C = p_i51048_14_;
      this.maxAge = 4;
      this.particleGravity = 0.008F;
      this.motionX = p_i51048_8_;
      this.motionY = p_i51048_10_;
      this.motionZ = p_i51048_12_;
      this.selectSpriteWithAge(p_i51048_14_);
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.age++ >= this.maxAge) {
         this.setExpired();
      } else {
         this.motionY -= (double)this.particleGravity;
         this.move(this.motionX, this.motionY, this.motionZ);
         this.selectSpriteWithAge(this.field_217573_C);
      }
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public Factory(IAnimatedSprite p_i49967_1_) {
         this.spriteSet = p_i49967_1_;
      }

      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new BubblePopParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
      }
   }
}