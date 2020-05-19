package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ReuseableStream;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class Particle {
   private static final AxisAlignedBB EMPTY_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
   protected final World world;
   protected double prevPosX;
   protected double prevPosY;
   protected double prevPosZ;
   protected double posX;
   protected double posY;
   protected double posZ;
   protected double motionX;
   protected double motionY;
   protected double motionZ;
   private AxisAlignedBB boundingBox = EMPTY_AABB;
   protected boolean onGround;
   protected boolean canCollide = true;
   private boolean collidedY;
   protected boolean isExpired;
   protected float width = 0.6F;
   protected float height = 1.8F;
   protected final Random rand = new Random();
   protected int age;
   protected int maxAge;
   protected float particleGravity;
   protected float particleRed = 1.0F;
   protected float particleGreen = 1.0F;
   protected float particleBlue = 1.0F;
   protected float particleAlpha = 1.0F;
   protected float particleAngle;
   protected float prevParticleAngle;

   protected Particle(World worldIn, double posXIn, double posYIn, double posZIn) {
      this.world = worldIn;
      this.setSize(0.2F, 0.2F);
      this.setPosition(posXIn, posYIn, posZIn);
      this.prevPosX = posXIn;
      this.prevPosY = posYIn;
      this.prevPosZ = posZIn;
      this.maxAge = (int)(4.0F / (this.rand.nextFloat() * 0.9F + 0.1F));
   }

   public Particle(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
      this(worldIn, xCoordIn, yCoordIn, zCoordIn);
      this.motionX = xSpeedIn + (Math.random() * 2.0D - 1.0D) * (double)0.4F;
      this.motionY = ySpeedIn + (Math.random() * 2.0D - 1.0D) * (double)0.4F;
      this.motionZ = zSpeedIn + (Math.random() * 2.0D - 1.0D) * (double)0.4F;
      float f = (float)(Math.random() + Math.random() + 1.0D) * 0.15F;
      float f1 = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
      this.motionX = this.motionX / (double)f1 * (double)f * (double)0.4F;
      this.motionY = this.motionY / (double)f1 * (double)f * (double)0.4F + (double)0.1F;
      this.motionZ = this.motionZ / (double)f1 * (double)f * (double)0.4F;
   }

   public Particle multiplyVelocity(float multiplier) {
      this.motionX *= (double)multiplier;
      this.motionY = (this.motionY - (double)0.1F) * (double)multiplier + (double)0.1F;
      this.motionZ *= (double)multiplier;
      return this;
   }

   public Particle multipleParticleScaleBy(float scale) {
      this.setSize(0.2F * scale, 0.2F * scale);
      return this;
   }

   public void setColor(float particleRedIn, float particleGreenIn, float particleBlueIn) {
      this.particleRed = particleRedIn;
      this.particleGreen = particleGreenIn;
      this.particleBlue = particleBlueIn;
   }

   /**
    * Sets the particle alpha (float)
    */
   protected void setAlphaF(float alpha) {
      this.particleAlpha = alpha;
   }

   public void setMaxAge(int particleLifeTime) {
      this.maxAge = particleLifeTime;
   }

   public int getMaxAge() {
      return this.maxAge;
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.age++ >= this.maxAge) {
         this.setExpired();
      } else {
         this.motionY -= 0.04D * (double)this.particleGravity;
         this.move(this.motionX, this.motionY, this.motionZ);
         this.motionX *= (double)0.98F;
         this.motionY *= (double)0.98F;
         this.motionZ *= (double)0.98F;
         if (this.onGround) {
            this.motionX *= (double)0.7F;
            this.motionZ *= (double)0.7F;
         }

      }
   }

   public abstract void renderParticle(IVertexBuilder buffer, ActiveRenderInfo renderInfo, float partialTicks);

   public abstract IParticleRenderType getRenderType();

   public String toString() {
      return this.getClass().getSimpleName() + ", Pos (" + this.posX + "," + this.posY + "," + this.posZ + "), RGBA (" + this.particleRed + "," + this.particleGreen + "," + this.particleBlue + "," + this.particleAlpha + "), Age " + this.age;
   }

   /**
    * Called to indicate that this particle effect has expired and should be discontinued.
    */
   public void setExpired() {
      this.isExpired = true;
   }

   protected void setSize(float particleWidth, float particleHeight) {
      if (particleWidth != this.width || particleHeight != this.height) {
         this.width = particleWidth;
         this.height = particleHeight;
         AxisAlignedBB axisalignedbb = this.getBoundingBox();
         double d0 = (axisalignedbb.minX + axisalignedbb.maxX - (double)particleWidth) / 2.0D;
         double d1 = (axisalignedbb.minZ + axisalignedbb.maxZ - (double)particleWidth) / 2.0D;
         this.setBoundingBox(new AxisAlignedBB(d0, axisalignedbb.minY, d1, d0 + (double)this.width, axisalignedbb.minY + (double)this.height, d1 + (double)this.width));
      }

   }

   public void setPosition(double x, double y, double z) {
      this.posX = x;
      this.posY = y;
      this.posZ = z;
      float f = this.width / 2.0F;
      float f1 = this.height;
      this.setBoundingBox(new AxisAlignedBB(x - (double)f, y, z - (double)f, x + (double)f, y + (double)f1, z + (double)f));
   }

   public void move(double x, double y, double z) {
      if (!this.collidedY) {
         double d0 = x;
         double d1 = y;
         double origZ = z;
         if (this.canCollide && (x != 0.0D || y != 0.0D || z != 0.0D)) {
            Vec3d vec3d = Entity.collideBoundingBoxHeuristically((Entity)null, new Vec3d(x, y, z), this.getBoundingBox(), this.world, ISelectionContext.dummy(), new ReuseableStream<>(Stream.empty()));
            x = vec3d.x;
            y = vec3d.y;
            z = vec3d.z;
         }

         if (x != 0.0D || y != 0.0D || z != 0.0D) {
            this.setBoundingBox(this.getBoundingBox().offset(x, y, z));
            this.resetPositionToBB();
         }

         if (Math.abs(d1) >= (double)1.0E-5F && Math.abs(y) < (double)1.0E-5F) {
            this.collidedY = true;
         }

         this.onGround = d1 != y && d1 < 0.0D;
         if (d0 != x) {
            this.motionX = 0.0D;
         }

         if (origZ != z) {
            this.motionZ = 0.0D;
         }

      }
   }

   protected void resetPositionToBB() {
      AxisAlignedBB axisalignedbb = this.getBoundingBox();
      this.posX = (axisalignedbb.minX + axisalignedbb.maxX) / 2.0D;
      this.posY = axisalignedbb.minY;
      this.posZ = (axisalignedbb.minZ + axisalignedbb.maxZ) / 2.0D;
   }

   protected int getBrightnessForRender(float partialTick) {
      BlockPos blockpos = new BlockPos(this.posX, this.posY, this.posZ);
      return this.world.isBlockLoaded(blockpos) ? WorldRenderer.getCombinedLight(this.world, blockpos) : 0;
   }

   /**
    * Returns true if this effect has not yet expired. "I feel happy! I feel happy!"
    */
   public boolean isAlive() {
      return !this.isExpired;
   }

   public AxisAlignedBB getBoundingBox() {
      return this.boundingBox;
   }

   public void setBoundingBox(AxisAlignedBB bb) {
      this.boundingBox = bb;
   }
}