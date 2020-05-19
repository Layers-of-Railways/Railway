package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FallingDustParticle extends SpriteTexturedParticle {
   private final float rotSpeed;
   private final IAnimatedSprite field_217580_F;

   private FallingDustParticle(World p_i51033_1_, double p_i51033_2_, double p_i51033_4_, double p_i51033_6_, float p_i51033_8_, float p_i51033_9_, float p_i51033_10_, IAnimatedSprite p_i51033_11_) {
      super(p_i51033_1_, p_i51033_2_, p_i51033_4_, p_i51033_6_);
      this.field_217580_F = p_i51033_11_;
      this.particleRed = p_i51033_8_;
      this.particleGreen = p_i51033_9_;
      this.particleBlue = p_i51033_10_;
      float f = 0.9F;
      this.particleScale *= 0.67499995F;
      int i = (int)(32.0D / (Math.random() * 0.8D + 0.2D));
      this.maxAge = (int)Math.max((float)i * 0.9F, 1.0F);
      this.selectSpriteWithAge(p_i51033_11_);
      this.rotSpeed = ((float)Math.random() - 0.5F) * 0.1F;
      this.particleAngle = (float)Math.random() * ((float)Math.PI * 2F);
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
         this.selectSpriteWithAge(this.field_217580_F);
         this.prevParticleAngle = this.particleAngle;
         this.particleAngle += (float)Math.PI * this.rotSpeed * 2.0F;
         if (this.onGround) {
            this.prevParticleAngle = this.particleAngle = 0.0F;
         }

         this.move(this.motionX, this.motionY, this.motionZ);
         this.motionY -= (double)0.003F;
         this.motionY = Math.max(this.motionY, (double)-0.14F);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BlockParticleData> {
      private final IAnimatedSprite spriteSet;

      public Factory(IAnimatedSprite spriteSetIn) {
         this.spriteSet = spriteSetIn;
      }

      @Nullable
      public Particle makeParticle(BlockParticleData typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         BlockState blockstate = typeIn.getBlockState();
         if (!blockstate.isAir() && blockstate.getRenderType() == BlockRenderType.INVISIBLE) {
            return null;
         } else {
            int i = Minecraft.getInstance().getBlockColors().getColorOrMaterialColor(blockstate, worldIn, new BlockPos(x, y, z));
            if (blockstate.getBlock() instanceof FallingBlock) {
               i = ((FallingBlock)blockstate.getBlock()).getDustColor(blockstate);
            }

            float f = (float)(i >> 16 & 255) / 255.0F;
            float f1 = (float)(i >> 8 & 255) / 255.0F;
            float f2 = (float)(i & 255) / 255.0F;
            return new FallingDustParticle(worldIn, x, y, z, f, f1, f2, this.spriteSet);
         }
      }
   }
}