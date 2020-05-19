package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class TexturedParticle extends Particle {
   protected float particleScale = 0.1F * (this.rand.nextFloat() * 0.5F + 0.5F) * 2.0F;

   protected TexturedParticle(World worldIn, double posXIn, double posYIn, double posZIn) {
      super(worldIn, posXIn, posYIn, posZIn);
   }

   protected TexturedParticle(World worldIn, double xCoord, double yCoord, double zCoord, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
      super(worldIn, xCoord, yCoord, zCoord, xSpeedIn, ySpeedIn, zSpeedIn);
   }

   public void renderParticle(IVertexBuilder buffer, ActiveRenderInfo renderInfo, float partialTicks) {
      Vec3d vec3d = renderInfo.getProjectedView();
      float f = (float)(MathHelper.lerp((double)partialTicks, this.prevPosX, this.posX) - vec3d.getX());
      float f1 = (float)(MathHelper.lerp((double)partialTicks, this.prevPosY, this.posY) - vec3d.getY());
      float f2 = (float)(MathHelper.lerp((double)partialTicks, this.prevPosZ, this.posZ) - vec3d.getZ());
      Quaternion quaternion;
      if (this.particleAngle == 0.0F) {
         quaternion = renderInfo.getRotation();
      } else {
         quaternion = new Quaternion(renderInfo.getRotation());
         float f3 = MathHelper.lerp(partialTicks, this.prevParticleAngle, this.particleAngle);
         quaternion.multiply(Vector3f.ZP.rotation(f3));
      }

      Vector3f vector3f1 = new Vector3f(-1.0F, -1.0F, 0.0F);
      vector3f1.transform(quaternion);
      Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
      float f4 = this.getScale(partialTicks);

      for(int i = 0; i < 4; ++i) {
         Vector3f vector3f = avector3f[i];
         vector3f.transform(quaternion);
         vector3f.mul(f4);
         vector3f.add(f, f1, f2);
      }

      float f7 = this.getMinU();
      float f8 = this.getMaxU();
      float f5 = this.getMinV();
      float f6 = this.getMaxV();
      int j = this.getBrightnessForRender(partialTicks);
      buffer.pos((double)avector3f[0].getX(), (double)avector3f[0].getY(), (double)avector3f[0].getZ()).tex(f8, f6).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j).endVertex();
      buffer.pos((double)avector3f[1].getX(), (double)avector3f[1].getY(), (double)avector3f[1].getZ()).tex(f8, f5).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j).endVertex();
      buffer.pos((double)avector3f[2].getX(), (double)avector3f[2].getY(), (double)avector3f[2].getZ()).tex(f7, f5).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j).endVertex();
      buffer.pos((double)avector3f[3].getX(), (double)avector3f[3].getY(), (double)avector3f[3].getZ()).tex(f7, f6).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j).endVertex();
   }

   public float getScale(float scaleFactor) {
      return this.particleScale;
   }

   public Particle multipleParticleScaleBy(float scale) {
      this.particleScale *= scale;
      return super.multipleParticleScaleBy(scale);
   }

   protected abstract float getMinU();

   protected abstract float getMaxU();

   protected abstract float getMinV();

   protected abstract float getMaxV();
}