package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RainParticle extends SpriteTexturedParticle {
   protected RainParticle(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0D, 0.0D, 0.0D);
      this.motionX *= (double)0.3F;
      this.motionY = Math.random() * (double)0.2F + (double)0.1F;
      this.motionZ *= (double)0.3F;
      this.setSize(0.01F, 0.01F);
      this.particleGravity = 0.06F;
      this.maxAge = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.maxAge-- <= 0) {
         this.setExpired();
      } else {
         this.motionY -= (double)this.particleGravity;
         this.move(this.motionX, this.motionY, this.motionZ);
         this.motionX *= (double)0.98F;
         this.motionY *= (double)0.98F;
         this.motionZ *= (double)0.98F;
         if (this.onGround) {
            if (Math.random() < 0.5D) {
               this.setExpired();
            }

            this.motionX *= (double)0.7F;
            this.motionZ *= (double)0.7F;
         }

         BlockPos blockpos = new BlockPos(this.posX, this.posY, this.posZ);
         double d0 = Math.max(this.world.getBlockState(blockpos).getCollisionShape(this.world, blockpos).max(Direction.Axis.Y, this.posX - (double)blockpos.getX(), this.posZ - (double)blockpos.getZ()), (double)this.world.getFluidState(blockpos).getActualHeight(this.world, blockpos));
         if (d0 > 0.0D && this.posY < (double)blockpos.getY() + d0) {
            this.setExpired();
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public Factory(IAnimatedSprite p_i50836_1_) {
         this.spriteSet = p_i50836_1_;
      }

      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         RainParticle rainparticle = new RainParticle(worldIn, x, y, z);
         rainparticle.selectSpriteRandomly(this.spriteSet);
         return rainparticle;
      }
   }
}