package net.minecraft.client.particle;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DripParticle extends SpriteTexturedParticle {
   private final Fluid fluid;

   private DripParticle(World p_i49197_1_, double p_i49197_2_, double p_i49197_4_, double p_i49197_6_, Fluid p_i49197_8_) {
      super(p_i49197_1_, p_i49197_2_, p_i49197_4_, p_i49197_6_);
      this.setSize(0.01F, 0.01F);
      this.particleGravity = 0.06F;
      this.fluid = p_i49197_8_;
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public int getBrightnessForRender(float partialTick) {
      return this.fluid.isIn(FluidTags.LAVA) ? 240 : super.getBrightnessForRender(partialTick);
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.func_217576_g();
      if (!this.isExpired) {
         this.motionY -= (double)this.particleGravity;
         this.move(this.motionX, this.motionY, this.motionZ);
         this.func_217577_h();
         if (!this.isExpired) {
            this.motionX *= (double)0.98F;
            this.motionY *= (double)0.98F;
            this.motionZ *= (double)0.98F;
            BlockPos blockpos = new BlockPos(this.posX, this.posY, this.posZ);
            IFluidState ifluidstate = this.world.getFluidState(blockpos);
            if (ifluidstate.getFluid() == this.fluid && this.posY < (double)((float)blockpos.getY() + ifluidstate.getActualHeight(this.world, blockpos))) {
               this.setExpired();
            }

         }
      }
   }

   protected void func_217576_g() {
      if (this.maxAge-- <= 0) {
         this.setExpired();
      }

   }

   protected void func_217577_h() {
   }

   @OnlyIn(Dist.CLIENT)
   static class Dripping extends DripParticle {
      private final IParticleData field_217579_C;

      private Dripping(World p_i50509_1_, double p_i50509_2_, double p_i50509_4_, double p_i50509_6_, Fluid p_i50509_8_, IParticleData p_i50509_9_) {
         super(p_i50509_1_, p_i50509_2_, p_i50509_4_, p_i50509_6_, p_i50509_8_);
         this.field_217579_C = p_i50509_9_;
         this.particleGravity *= 0.02F;
         this.maxAge = 40;
      }

      protected void func_217576_g() {
         if (this.maxAge-- <= 0) {
            this.setExpired();
            this.world.addParticle(this.field_217579_C, this.posX, this.posY, this.posZ, this.motionX, this.motionY, this.motionZ);
         }

      }

      protected void func_217577_h() {
         this.motionX *= 0.02D;
         this.motionY *= 0.02D;
         this.motionZ *= 0.02D;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class DrippingHoneyFactory implements IParticleFactory<BasicParticleType> {
      protected final IAnimatedSprite field_228337_a_;

      public DrippingHoneyFactory(IAnimatedSprite p_i225960_1_) {
         this.field_228337_a_ = p_i225960_1_;
      }

      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         DripParticle.Dripping dripparticle$dripping = new DripParticle.Dripping(worldIn, x, y, z, Fluids.EMPTY, ParticleTypes.FALLING_HONEY);
         dripparticle$dripping.particleGravity *= 0.01F;
         dripparticle$dripping.maxAge = 100;
         dripparticle$dripping.setColor(0.622F, 0.508F, 0.082F);
         dripparticle$dripping.selectSpriteRandomly(this.field_228337_a_);
         return dripparticle$dripping;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class DrippingLava extends DripParticle.Dripping {
      private DrippingLava(World p_i50513_1_, double p_i50513_2_, double p_i50513_4_, double p_i50513_6_, Fluid p_i50513_8_, IParticleData p_i50513_9_) {
         super(p_i50513_1_, p_i50513_2_, p_i50513_4_, p_i50513_6_, p_i50513_8_, p_i50513_9_);
      }

      protected void func_217576_g() {
         this.particleRed = 1.0F;
         this.particleGreen = 16.0F / (float)(40 - this.maxAge + 16);
         this.particleBlue = 4.0F / (float)(40 - this.maxAge + 8);
         super.func_217576_g();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class DrippingLavaFactory implements IParticleFactory<BasicParticleType> {
      protected final IAnimatedSprite spriteSet;

      public DrippingLavaFactory(IAnimatedSprite p_i50505_1_) {
         this.spriteSet = p_i50505_1_;
      }

      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         DripParticle.DrippingLava dripparticle$drippinglava = new DripParticle.DrippingLava(worldIn, x, y, z, Fluids.LAVA, ParticleTypes.FALLING_LAVA);
         dripparticle$drippinglava.selectSpriteRandomly(this.spriteSet);
         return dripparticle$drippinglava;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class DrippingWaterFactory implements IParticleFactory<BasicParticleType> {
      protected final IAnimatedSprite spriteSet;

      public DrippingWaterFactory(IAnimatedSprite p_i50502_1_) {
         this.spriteSet = p_i50502_1_;
      }

      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         DripParticle dripparticle = new DripParticle.Dripping(worldIn, x, y, z, Fluids.WATER, ParticleTypes.FALLING_WATER);
         dripparticle.setColor(0.2F, 0.3F, 1.0F);
         dripparticle.selectSpriteRandomly(this.spriteSet);
         return dripparticle;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class FallingHoneyFactory implements IParticleFactory<BasicParticleType> {
      protected final IAnimatedSprite field_228336_a_;

      public FallingHoneyFactory(IAnimatedSprite p_i225959_1_) {
         this.field_228336_a_ = p_i225959_1_;
      }

      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         DripParticle dripparticle = new DripParticle.FallingHoneyParticle(worldIn, x, y, z, Fluids.EMPTY, ParticleTypes.LANDING_HONEY);
         dripparticle.particleGravity = 0.01F;
         dripparticle.setColor(0.582F, 0.448F, 0.082F);
         dripparticle.selectSpriteRandomly(this.field_228336_a_);
         return dripparticle;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class FallingHoneyParticle extends DripParticle.FallingLiquidParticle {
      private FallingHoneyParticle(World p_i225957_1_, double p_i225957_2_, double p_i225957_4_, double p_i225957_6_, Fluid p_i225957_8_, IParticleData p_i225957_9_) {
         super(p_i225957_1_, p_i225957_2_, p_i225957_4_, p_i225957_6_, p_i225957_8_, p_i225957_9_);
      }

      protected void func_217577_h() {
         if (this.onGround) {
            this.setExpired();
            this.world.addParticle(this.field_228335_a_, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
            this.world.playSound(this.posX + 0.5D, this.posY, this.posZ + 0.5D, SoundEvents.BLOCK_BEEHIVE_DROP, SoundCategory.BLOCKS, 0.3F + this.world.rand.nextFloat() * 2.0F / 3.0F, 1.0F, false);
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class FallingLavaFactory implements IParticleFactory<BasicParticleType> {
      protected final IAnimatedSprite spriteSet;

      public FallingLavaFactory(IAnimatedSprite p_i50506_1_) {
         this.spriteSet = p_i50506_1_;
      }

      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         DripParticle dripparticle = new DripParticle.FallingLiquidParticle(worldIn, x, y, z, Fluids.LAVA, ParticleTypes.LANDING_LAVA);
         dripparticle.setColor(1.0F, 0.2857143F, 0.083333336F);
         dripparticle.selectSpriteRandomly(this.spriteSet);
         return dripparticle;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class FallingLiquidParticle extends DripParticle.FallingNectarParticle {
      protected final IParticleData field_228335_a_;

      private FallingLiquidParticle(World p_i225953_1_, double p_i225953_2_, double p_i225953_4_, double p_i225953_6_, Fluid p_i225953_8_, IParticleData p_i225953_9_) {
         super(p_i225953_1_, p_i225953_2_, p_i225953_4_, p_i225953_6_, p_i225953_8_);
         this.field_228335_a_ = p_i225953_9_;
      }

      protected void func_217577_h() {
         if (this.onGround) {
            this.setExpired();
            this.world.addParticle(this.field_228335_a_, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class FallingNectarFactory implements IParticleFactory<BasicParticleType> {
      protected final IAnimatedSprite field_228339_a_;

      public FallingNectarFactory(IAnimatedSprite p_i225962_1_) {
         this.field_228339_a_ = p_i225962_1_;
      }

      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         DripParticle dripparticle = new DripParticle.FallingNectarParticle(worldIn, x, y, z, Fluids.EMPTY);
         dripparticle.maxAge = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
         dripparticle.particleGravity = 0.007F;
         dripparticle.setColor(0.92F, 0.782F, 0.72F);
         dripparticle.selectSpriteRandomly(this.field_228339_a_);
         return dripparticle;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class FallingNectarParticle extends DripParticle {
      private FallingNectarParticle(World p_i225955_1_, double p_i225955_2_, double p_i225955_4_, double p_i225955_6_, Fluid p_i225955_8_) {
         super(p_i225955_1_, p_i225955_2_, p_i225955_4_, p_i225955_6_, p_i225955_8_);
         this.maxAge = (int)(64.0D / (Math.random() * 0.8D + 0.2D));
      }

      protected void func_217577_h() {
         if (this.onGround) {
            this.setExpired();
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class FallingWaterFactory implements IParticleFactory<BasicParticleType> {
      protected final IAnimatedSprite spriteSet;

      public FallingWaterFactory(IAnimatedSprite p_i50503_1_) {
         this.spriteSet = p_i50503_1_;
      }

      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         DripParticle dripparticle = new DripParticle.FallingLiquidParticle(worldIn, x, y, z, Fluids.WATER, ParticleTypes.SPLASH);
         dripparticle.setColor(0.2F, 0.3F, 1.0F);
         dripparticle.selectSpriteRandomly(this.spriteSet);
         return dripparticle;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class Landing extends DripParticle {
      private Landing(World p_i50507_1_, double p_i50507_2_, double p_i50507_4_, double p_i50507_6_, Fluid p_i50507_8_) {
         super(p_i50507_1_, p_i50507_2_, p_i50507_4_, p_i50507_6_, p_i50507_8_);
         this.maxAge = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class LandingHoneyFactory implements IParticleFactory<BasicParticleType> {
      protected final IAnimatedSprite field_228338_a_;

      public LandingHoneyFactory(IAnimatedSprite p_i225961_1_) {
         this.field_228338_a_ = p_i225961_1_;
      }

      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         DripParticle dripparticle = new DripParticle.Landing(worldIn, x, y, z, Fluids.EMPTY);
         dripparticle.maxAge = (int)(128.0D / (Math.random() * 0.8D + 0.2D));
         dripparticle.setColor(0.522F, 0.408F, 0.082F);
         dripparticle.selectSpriteRandomly(this.field_228338_a_);
         return dripparticle;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class LandingLavaFactory implements IParticleFactory<BasicParticleType> {
      protected final IAnimatedSprite spriteSet;

      public LandingLavaFactory(IAnimatedSprite p_i50504_1_) {
         this.spriteSet = p_i50504_1_;
      }

      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         DripParticle dripparticle = new DripParticle.Landing(worldIn, x, y, z, Fluids.LAVA);
         dripparticle.setColor(1.0F, 0.2857143F, 0.083333336F);
         dripparticle.selectSpriteRandomly(this.spriteSet);
         return dripparticle;
      }
   }
}