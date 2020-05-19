package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import net.minecraft.client.renderer.Matrix3f;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.MemoryStack;

@OnlyIn(Dist.CLIENT)
public interface IVertexBuilder extends net.minecraftforge.client.extensions.IForgeVertexBuilder {
   Logger LOGGER = LogManager.getLogger();

   IVertexBuilder pos(double x, double y, double z);

   IVertexBuilder color(int red, int green, int blue, int alpha);

   IVertexBuilder tex(float u, float v);

   IVertexBuilder overlay(int u, int v);

   IVertexBuilder lightmap(int u, int v);

   IVertexBuilder normal(float x, float y, float z);

   void endVertex();

   default void addVertex(float x, float y, float z, float red, float green, float blue, float alpha, float texU, float texV, int overlayUV, int lightmapUV, float normalX, float normalY, float normalZ) {
      this.pos((double)x, (double)y, (double)z);
      this.color(red, green, blue, alpha);
      this.tex(texU, texV);
      this.overlay(overlayUV);
      this.lightmap(lightmapUV);
      this.normal(normalX, normalY, normalZ);
      this.endVertex();
   }

   default IVertexBuilder color(float red, float green, float blue, float alpha) {
      return this.color((int)(red * 255.0F), (int)(green * 255.0F), (int)(blue * 255.0F), (int)(alpha * 255.0F));
   }

   default IVertexBuilder lightmap(int lightmapUV) {
      return this.lightmap(lightmapUV & '\uffff', lightmapUV >> 16 & '\uffff');
   }

   default IVertexBuilder overlay(int overlayUV) {
      return this.overlay(overlayUV & '\uffff', overlayUV >> 16 & '\uffff');
   }

   default void addQuad(MatrixStack.Entry matrixEntryIn, BakedQuad quadIn, float redIn, float greenIn, float blueIn, int combinedLightIn, int combinedOverlayIn) {
      this.addQuad(matrixEntryIn, quadIn, new float[]{1.0F, 1.0F, 1.0F, 1.0F}, redIn, greenIn, blueIn, new int[]{combinedLightIn, combinedLightIn, combinedLightIn, combinedLightIn}, combinedOverlayIn, false);
   }

   default void addQuad(MatrixStack.Entry matrixEntryIn, BakedQuad quadIn, float[] colorMuls, float redIn, float greenIn, float blueIn, int[] combinedLightsIn, int combinedOverlayIn, boolean mulColor) {
      int[] aint = quadIn.getVertexData();
      Vec3i vec3i = quadIn.getFace().getDirectionVec();
      Vector3f vector3f = new Vector3f((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
      Matrix4f matrix4f = matrixEntryIn.getMatrix();
      vector3f.transform(matrixEntryIn.getNormal());
      int i = 8;
      int j = aint.length / 8;

      try (MemoryStack memorystack = MemoryStack.stackPush()) {
         ByteBuffer bytebuffer = memorystack.malloc(DefaultVertexFormats.BLOCK.getSize());
         IntBuffer intbuffer = bytebuffer.asIntBuffer();

         for(int k = 0; k < j; ++k) {
            ((Buffer)intbuffer).clear();
            intbuffer.put(aint, k * 8, 8);
            float f = bytebuffer.getFloat(0);
            float f1 = bytebuffer.getFloat(4);
            float f2 = bytebuffer.getFloat(8);
            float f3;
            float f4;
            float f5;
            if (mulColor) {
               float f6 = (float)(bytebuffer.get(12) & 255) / 255.0F;
               float f7 = (float)(bytebuffer.get(13) & 255) / 255.0F;
               float f8 = (float)(bytebuffer.get(14) & 255) / 255.0F;
               f3 = f6 * colorMuls[k] * redIn;
               f4 = f7 * colorMuls[k] * greenIn;
               f5 = f8 * colorMuls[k] * blueIn;
            } else {
               f3 = colorMuls[k] * redIn;
               f4 = colorMuls[k] * greenIn;
               f5 = colorMuls[k] * blueIn;
            }

            int l = applyBakedLighting(combinedLightsIn[k], bytebuffer);
            float f9 = bytebuffer.getFloat(16);
            float f10 = bytebuffer.getFloat(20);
            Vector4f vector4f = new Vector4f(f, f1, f2, 1.0F);
            vector4f.transform(matrix4f);
            applyBakedNormals(vector3f, bytebuffer, matrixEntryIn.getNormal());
            this.addVertex(vector4f.getX(), vector4f.getY(), vector4f.getZ(), f3, f4, f5, 1.0F, f9, f10, combinedOverlayIn, l, vector3f.getX(), vector3f.getY(), vector3f.getZ());
         }
      }

   }

   default IVertexBuilder pos(Matrix4f matrixIn, float x, float y, float z) {
      Vector4f vector4f = new Vector4f(x, y, z, 1.0F);
      vector4f.transform(matrixIn);
      return this.pos((double)vector4f.getX(), (double)vector4f.getY(), (double)vector4f.getZ());
   }

   default IVertexBuilder normal(Matrix3f matrixIn, float x, float y, float z) {
      Vector3f vector3f = new Vector3f(x, y, z);
      vector3f.transform(matrixIn);
      return this.normal(vector3f.getX(), vector3f.getY(), vector3f.getZ());
   }
}