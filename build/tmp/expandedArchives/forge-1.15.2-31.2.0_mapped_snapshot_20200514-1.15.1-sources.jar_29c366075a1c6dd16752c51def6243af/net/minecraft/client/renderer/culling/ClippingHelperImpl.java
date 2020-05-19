package net.minecraft.client.renderer.culling;

import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClippingHelperImpl {
   private final Vector4f[] frustum = new Vector4f[6];
   private double cameraX;
   private double cameraY;
   private double cameraZ;

   public ClippingHelperImpl(Matrix4f p_i226026_1_, Matrix4f p_i226026_2_) {
      this.calculateFrustum(p_i226026_1_, p_i226026_2_);
   }

   public void setCameraPosition(double camX, double camY, double camZ) {
      this.cameraX = camX;
      this.cameraY = camY;
      this.cameraZ = camZ;
   }

   private void calculateFrustum(Matrix4f p_228956_1_, Matrix4f p_228956_2_) {
      Matrix4f matrix4f = p_228956_2_.copy();
      matrix4f.mul(p_228956_1_);
      matrix4f.transpose();
      this.setFrustumPlane(matrix4f, -1, 0, 0, 0);
      this.setFrustumPlane(matrix4f, 1, 0, 0, 1);
      this.setFrustumPlane(matrix4f, 0, -1, 0, 2);
      this.setFrustumPlane(matrix4f, 0, 1, 0, 3);
      this.setFrustumPlane(matrix4f, 0, 0, -1, 4);
      this.setFrustumPlane(matrix4f, 0, 0, 1, 5);
   }

   private void setFrustumPlane(Matrix4f p_228955_1_, int p_228955_2_, int p_228955_3_, int p_228955_4_, int p_228955_5_) {
      Vector4f vector4f = new Vector4f((float)p_228955_2_, (float)p_228955_3_, (float)p_228955_4_, 1.0F);
      vector4f.transform(p_228955_1_);
      vector4f.normalize();
      this.frustum[p_228955_5_] = vector4f;
   }

   public boolean isBoundingBoxInFrustum(AxisAlignedBB aabbIn) {
      return this.isBoxInFrustum(aabbIn.minX, aabbIn.minY, aabbIn.minZ, aabbIn.maxX, aabbIn.maxY, aabbIn.maxZ);
   }

   private boolean isBoxInFrustum(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
      float f = (float)(minX - this.cameraX);
      float f1 = (float)(minY - this.cameraY);
      float f2 = (float)(minZ - this.cameraZ);
      float f3 = (float)(maxX - this.cameraX);
      float f4 = (float)(maxY - this.cameraY);
      float f5 = (float)(maxZ - this.cameraZ);
      return this.isBoxInFrustumRaw(f, f1, f2, f3, f4, f5);
   }

   private boolean isBoxInFrustumRaw(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
      for(int i = 0; i < 6; ++i) {
         Vector4f vector4f = this.frustum[i];
         if (!(vector4f.dot(new Vector4f(minX, minY, minZ, 1.0F)) > 0.0F) && !(vector4f.dot(new Vector4f(maxX, minY, minZ, 1.0F)) > 0.0F) && !(vector4f.dot(new Vector4f(minX, maxY, minZ, 1.0F)) > 0.0F) && !(vector4f.dot(new Vector4f(maxX, maxY, minZ, 1.0F)) > 0.0F) && !(vector4f.dot(new Vector4f(minX, minY, maxZ, 1.0F)) > 0.0F) && !(vector4f.dot(new Vector4f(maxX, minY, maxZ, 1.0F)) > 0.0F) && !(vector4f.dot(new Vector4f(minX, maxY, maxZ, 1.0F)) > 0.0F) && !(vector4f.dot(new Vector4f(maxX, maxY, maxZ, 1.0F)) > 0.0F)) {
            return false;
         }
      }

      return true;
   }
}