package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.item.DyeColor;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FireworkParticle {
   @OnlyIn(Dist.CLIENT)
   public static class Overlay extends SpriteTexturedParticle {
      private Overlay(World p_i46466_1_, double p_i46466_2_, double p_i46466_4_, double p_i46466_6_) {
         super(p_i46466_1_, p_i46466_2_, p_i46466_4_, p_i46466_6_);
         this.maxAge = 4;
      }

      public IParticleRenderType getRenderType() {
         return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
      }

      public void renderParticle(IVertexBuilder buffer, ActiveRenderInfo renderInfo, float partialTicks) {
         this.setAlphaF(0.6F - ((float)this.age + partialTicks - 1.0F) * 0.25F * 0.5F);
         super.renderParticle(buffer, renderInfo, partialTicks);
      }

      public float getScale(float scaleFactor) {
         return 7.1F * MathHelper.sin(((float)this.age + scaleFactor - 1.0F) * 0.25F * (float)Math.PI);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class OverlayFactory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public OverlayFactory(IAnimatedSprite p_i50889_1_) {
         this.spriteSet = p_i50889_1_;
      }

      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         FireworkParticle.Overlay fireworkparticle$overlay = new FireworkParticle.Overlay(worldIn, x, y, z);
         fireworkparticle$overlay.selectSpriteRandomly(this.spriteSet);
         return fireworkparticle$overlay;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class Spark extends SimpleAnimatedParticle {
      private boolean trail;
      private boolean twinkle;
      private final ParticleManager effectRenderer;
      private float fadeColourRed;
      private float fadeColourGreen;
      private float fadeColourBlue;
      private boolean hasFadeColour;

      private Spark(World p_i50884_1_, double p_i50884_2_, double p_i50884_4_, double p_i50884_6_, double p_i50884_8_, double p_i50884_10_, double p_i50884_12_, ParticleManager p_i50884_14_, IAnimatedSprite p_i50884_15_) {
         super(p_i50884_1_, p_i50884_2_, p_i50884_4_, p_i50884_6_, p_i50884_15_, -0.004F);
         this.motionX = p_i50884_8_;
         this.motionY = p_i50884_10_;
         this.motionZ = p_i50884_12_;
         this.effectRenderer = p_i50884_14_;
         this.particleScale *= 0.75F;
         this.maxAge = 48 + this.rand.nextInt(12);
         this.selectSpriteWithAge(p_i50884_15_);
      }

      public void setTrail(boolean trailIn) {
         this.trail = trailIn;
      }

      public void setTwinkle(boolean twinkleIn) {
         this.twinkle = twinkleIn;
      }

      public void renderParticle(IVertexBuilder buffer, ActiveRenderInfo renderInfo, float partialTicks) {
         if (!this.twinkle || this.age < this.maxAge / 3 || (this.age + this.maxAge) / 3 % 2 == 0) {
            super.renderParticle(buffer, renderInfo, partialTicks);
         }

      }

      public void tick() {
         super.tick();
         if (this.trail && this.age < this.maxAge / 2 && (this.age + this.maxAge) % 2 == 0) {
            FireworkParticle.Spark fireworkparticle$spark = new FireworkParticle.Spark(this.world, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D, this.effectRenderer, this.field_217584_C);
            fireworkparticle$spark.setAlphaF(0.99F);
            fireworkparticle$spark.setColor(this.particleRed, this.particleGreen, this.particleBlue);
            fireworkparticle$spark.age = fireworkparticle$spark.maxAge / 2;
            if (this.hasFadeColour) {
               fireworkparticle$spark.hasFadeColour = true;
               fireworkparticle$spark.fadeColourRed = this.fadeColourRed;
               fireworkparticle$spark.fadeColourGreen = this.fadeColourGreen;
               fireworkparticle$spark.fadeColourBlue = this.fadeColourBlue;
            }

            fireworkparticle$spark.twinkle = this.twinkle;
            this.effectRenderer.addEffect(fireworkparticle$spark);
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class SparkFactory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public SparkFactory(IAnimatedSprite p_i50883_1_) {
         this.spriteSet = p_i50883_1_;
      }

      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         FireworkParticle.Spark fireworkparticle$spark = new FireworkParticle.Spark(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, Minecraft.getInstance().particles, this.spriteSet);
         fireworkparticle$spark.setAlphaF(0.99F);
         return fireworkparticle$spark;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Starter extends MetaParticle {
      private int fireworkAge;
      private final ParticleManager manager;
      private ListNBT fireworkExplosions;
      private boolean twinkle;

      public Starter(World p_i46464_1_, double p_i46464_2_, double p_i46464_4_, double p_i46464_6_, double p_i46464_8_, double p_i46464_10_, double p_i46464_12_, ParticleManager p_i46464_14_, @Nullable CompoundNBT p_i46464_15_) {
         super(p_i46464_1_, p_i46464_2_, p_i46464_4_, p_i46464_6_);
         this.motionX = p_i46464_8_;
         this.motionY = p_i46464_10_;
         this.motionZ = p_i46464_12_;
         this.manager = p_i46464_14_;
         this.maxAge = 8;
         if (p_i46464_15_ != null) {
            this.fireworkExplosions = p_i46464_15_.getList("Explosions", 10);
            if (this.fireworkExplosions.isEmpty()) {
               this.fireworkExplosions = null;
            } else {
               this.maxAge = this.fireworkExplosions.size() * 2 - 1;

               for(int i = 0; i < this.fireworkExplosions.size(); ++i) {
                  CompoundNBT compoundnbt = this.fireworkExplosions.getCompound(i);
                  if (compoundnbt.getBoolean("Flicker")) {
                     this.twinkle = true;
                     this.maxAge += 15;
                     break;
                  }
               }
            }
         }

      }

      public void tick() {
         if (this.fireworkAge == 0 && this.fireworkExplosions != null) {
            boolean flag = this.isFarFromCamera();
            boolean flag1 = false;
            if (this.fireworkExplosions.size() >= 3) {
               flag1 = true;
            } else {
               for(int i = 0; i < this.fireworkExplosions.size(); ++i) {
                  CompoundNBT compoundnbt = this.fireworkExplosions.getCompound(i);
                  if (FireworkRocketItem.Shape.get(compoundnbt.getByte("Type")) == FireworkRocketItem.Shape.LARGE_BALL) {
                     flag1 = true;
                     break;
                  }
               }
            }

            SoundEvent soundevent1;
            if (flag1) {
               soundevent1 = flag ? SoundEvents.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR : SoundEvents.ENTITY_FIREWORK_ROCKET_LARGE_BLAST;
            } else {
               soundevent1 = flag ? SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST_FAR : SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST;
            }

            this.world.playSound(this.posX, this.posY, this.posZ, soundevent1, SoundCategory.AMBIENT, 20.0F, 0.95F + this.rand.nextFloat() * 0.1F, true);
         }

         if (this.fireworkAge % 2 == 0 && this.fireworkExplosions != null && this.fireworkAge / 2 < this.fireworkExplosions.size()) {
            int k = this.fireworkAge / 2;
            CompoundNBT compoundnbt1 = this.fireworkExplosions.getCompound(k);
            FireworkRocketItem.Shape fireworkrocketitem$shape = FireworkRocketItem.Shape.get(compoundnbt1.getByte("Type"));
            boolean flag4 = compoundnbt1.getBoolean("Trail");
            boolean flag2 = compoundnbt1.getBoolean("Flicker");
            int[] aint = compoundnbt1.getIntArray("Colors");
            int[] aint1 = compoundnbt1.getIntArray("FadeColors");
            if (aint.length == 0) {
               aint = new int[]{DyeColor.BLACK.getFireworkColor()};
            }

            switch(fireworkrocketitem$shape) {
            case SMALL_BALL:
            default:
               this.createBall(0.25D, 2, aint, aint1, flag4, flag2);
               break;
            case LARGE_BALL:
               this.createBall(0.5D, 4, aint, aint1, flag4, flag2);
               break;
            case STAR:
               this.createShaped(0.5D, new double[][]{{0.0D, 1.0D}, {0.3455D, 0.309D}, {0.9511D, 0.309D}, {0.3795918367346939D, -0.12653061224489795D}, {0.6122448979591837D, -0.8040816326530612D}, {0.0D, -0.35918367346938773D}}, aint, aint1, flag4, flag2, false);
               break;
            case CREEPER:
               this.createShaped(0.5D, new double[][]{{0.0D, 0.2D}, {0.2D, 0.2D}, {0.2D, 0.6D}, {0.6D, 0.6D}, {0.6D, 0.2D}, {0.2D, 0.2D}, {0.2D, 0.0D}, {0.4D, 0.0D}, {0.4D, -0.6D}, {0.2D, -0.6D}, {0.2D, -0.4D}, {0.0D, -0.4D}}, aint, aint1, flag4, flag2, true);
               break;
            case BURST:
               this.createBurst(aint, aint1, flag4, flag2);
            }

            int j = aint[0];
            float f = (float)((j & 16711680) >> 16) / 255.0F;
            float f1 = (float)((j & '\uff00') >> 8) / 255.0F;
            float f2 = (float)((j & 255) >> 0) / 255.0F;
            Particle particle = this.manager.addParticle(ParticleTypes.FLASH, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
            particle.setColor(f, f1, f2);
         }

         ++this.fireworkAge;
         if (this.fireworkAge > this.maxAge) {
            if (this.twinkle) {
               boolean flag3 = this.isFarFromCamera();
               SoundEvent soundevent = flag3 ? SoundEvents.ENTITY_FIREWORK_ROCKET_TWINKLE_FAR : SoundEvents.ENTITY_FIREWORK_ROCKET_TWINKLE;
               this.world.playSound(this.posX, this.posY, this.posZ, soundevent, SoundCategory.AMBIENT, 20.0F, 0.9F + this.rand.nextFloat() * 0.15F, true);
            }

            this.setExpired();
         }

      }

      private boolean isFarFromCamera() {
         Minecraft minecraft = Minecraft.getInstance();
         return minecraft.gameRenderer.getActiveRenderInfo().getProjectedView().squareDistanceTo(this.posX, this.posY, this.posZ) >= 256.0D;
      }

      /**
       * Creates a single particle.
       */
      private void createParticle(double p_92034_1_, double p_92034_3_, double p_92034_5_, double p_92034_7_, double p_92034_9_, double p_92034_11_, int[] p_92034_13_, int[] p_92034_14_, boolean p_92034_15_, boolean p_92034_16_) {
         FireworkParticle.Spark fireworkparticle$spark = (FireworkParticle.Spark)this.manager.addParticle(ParticleTypes.FIREWORK, p_92034_1_, p_92034_3_, p_92034_5_, p_92034_7_, p_92034_9_, p_92034_11_);
         fireworkparticle$spark.setTrail(p_92034_15_);
         fireworkparticle$spark.setTwinkle(p_92034_16_);
         fireworkparticle$spark.setAlphaF(0.99F);
         int i = this.rand.nextInt(p_92034_13_.length);
         fireworkparticle$spark.setColor(p_92034_13_[i]);
         if (p_92034_14_.length > 0) {
            fireworkparticle$spark.setColorFade(p_92034_14_[this.rand.nextInt(p_92034_14_.length)]);
         }

      }

      /**
       * Creates a small ball or large ball type explosion effect.
       */
      private void createBall(double speed, int size, int[] colours, int[] fadeColours, boolean trail, boolean twinkleIn) {
         double d0 = this.posX;
         double d1 = this.posY;
         double d2 = this.posZ;

         for(int i = -size; i <= size; ++i) {
            for(int j = -size; j <= size; ++j) {
               for(int k = -size; k <= size; ++k) {
                  double d3 = (double)j + (this.rand.nextDouble() - this.rand.nextDouble()) * 0.5D;
                  double d4 = (double)i + (this.rand.nextDouble() - this.rand.nextDouble()) * 0.5D;
                  double d5 = (double)k + (this.rand.nextDouble() - this.rand.nextDouble()) * 0.5D;
                  double d6 = (double)MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5) / speed + this.rand.nextGaussian() * 0.05D;
                  this.createParticle(d0, d1, d2, d3 / d6, d4 / d6, d5 / d6, colours, fadeColours, trail, twinkleIn);
                  if (i != -size && i != size && j != -size && j != size) {
                     k += size * 2 - 1;
                  }
               }
            }
         }

      }

      /**
       * Creates a creeper-shaped or star-shaped explosion.
       */
      private void createShaped(double speed, double[][] shape, int[] colours, int[] fadeColours, boolean trail, boolean twinkleIn, boolean p_92038_8_) {
         double d0 = shape[0][0];
         double d1 = shape[0][1];
         this.createParticle(this.posX, this.posY, this.posZ, d0 * speed, d1 * speed, 0.0D, colours, fadeColours, trail, twinkleIn);
         float f = this.rand.nextFloat() * (float)Math.PI;
         double d2 = p_92038_8_ ? 0.034D : 0.34D;

         for(int i = 0; i < 3; ++i) {
            double d3 = (double)f + (double)((float)i * (float)Math.PI) * d2;
            double d4 = d0;
            double d5 = d1;

            for(int j = 1; j < shape.length; ++j) {
               double d6 = shape[j][0];
               double d7 = shape[j][1];

               for(double d8 = 0.25D; d8 <= 1.0D; d8 += 0.25D) {
                  double d9 = MathHelper.lerp(d8, d4, d6) * speed;
                  double d10 = MathHelper.lerp(d8, d5, d7) * speed;
                  double d11 = d9 * Math.sin(d3);
                  d9 = d9 * Math.cos(d3);

                  for(double d12 = -1.0D; d12 <= 1.0D; d12 += 2.0D) {
                     this.createParticle(this.posX, this.posY, this.posZ, d9 * d12, d10, d11 * d12, colours, fadeColours, trail, twinkleIn);
                  }
               }

               d4 = d6;
               d5 = d7;
            }
         }

      }

      /**
       * Creates a burst type explosion effect.
       */
      private void createBurst(int[] colours, int[] fadeColours, boolean trail, boolean twinkleIn) {
         double d0 = this.rand.nextGaussian() * 0.05D;
         double d1 = this.rand.nextGaussian() * 0.05D;

         for(int i = 0; i < 70; ++i) {
            double d2 = this.motionX * 0.5D + this.rand.nextGaussian() * 0.15D + d0;
            double d3 = this.motionZ * 0.5D + this.rand.nextGaussian() * 0.15D + d1;
            double d4 = this.motionY * 0.5D + this.rand.nextDouble() * 0.5D;
            this.createParticle(this.posX, this.posY, this.posZ, d2, d4, d3, colours, fadeColours, trail, twinkleIn);
         }

      }
   }
}