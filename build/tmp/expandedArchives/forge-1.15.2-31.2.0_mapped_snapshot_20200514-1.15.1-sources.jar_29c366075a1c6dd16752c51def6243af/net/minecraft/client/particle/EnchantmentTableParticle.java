package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EnchantmentTableParticle extends SpriteTexturedParticle {
   private final double coordX;
   private final double coordY;
   private final double coordZ;

   private EnchantmentTableParticle(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn);
      this.motionX = xSpeedIn;
      this.motionY = ySpeedIn;
      this.motionZ = zSpeedIn;
      this.coordX = xCoordIn;
      this.coordY = yCoordIn;
      this.coordZ = zCoordIn;
      this.prevPosX = xCoordIn + xSpeedIn;
      this.prevPosY = yCoordIn + ySpeedIn;
      this.prevPosZ = zCoordIn + zSpeedIn;
      this.posX = this.prevPosX;
      this.posY = this.prevPosY;
      this.posZ = this.prevPosZ;
      this.particleScale = 0.1F * (this.rand.nextFloat() * 0.5F + 0.2F);
      float f = this.rand.nextFloat() * 0.6F + 0.4F;
      this.particleRed = 0.9F * f;
      this.particleGreen = 0.9F * f;
      this.particleBlue = f;
      this.canCollide = false;
      this.maxAge = (int)(Math.random() * 10.0D) + 30;
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public void move(double x, double y, double z) {
      this.setBoundingBox(this.getBoundingBox().offset(x, y, z));
      this.resetPositionToBB();
   }

   public int getBrightnessForRender(float partialTick) {
      int i = super.getBrightnessForRender(partialTick);
      float f = (float)this.age / (float)this.maxAge;
      f = f * f;
      f = f * f;
      int j = i & 255;
      int k = i >> 16 & 255;
      k = k + (int)(f * 15.0F * 16.0F);
      if (k > 240) {
         k = 240;
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
         float f = (float)this.age / (float)this.maxAge;
         f = 1.0F - f;
         float f1 = 1.0F - f;
         f1 = f1 * f1;
         f1 = f1 * f1;
         this.posX = this.coordX + this.motionX * (double)f;
         this.posY = this.coordY + this.motionY * (double)f - (double)(f1 * 1.2F);
         this.posZ = this.coordZ + this.motionZ * (double)f;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class EnchantmentTable implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public EnchantmentTable(IAnimatedSprite p_i50441_1_) {
         this.spriteSet = p_i50441_1_;
      }

      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         EnchantmentTableParticle enchantmenttableparticle = new EnchantmentTableParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
         enchantmenttableparticle.selectSpriteRandomly(this.spriteSet);
         return enchantmenttableparticle;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class NautilusFactory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public NautilusFactory(IAnimatedSprite p_i50442_1_) {
         this.spriteSet = p_i50442_1_;
      }

      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         EnchantmentTableParticle enchantmenttableparticle = new EnchantmentTableParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
         enchantmenttableparticle.selectSpriteRandomly(this.spriteSet);
         return enchantmenttableparticle;
      }
   }
}