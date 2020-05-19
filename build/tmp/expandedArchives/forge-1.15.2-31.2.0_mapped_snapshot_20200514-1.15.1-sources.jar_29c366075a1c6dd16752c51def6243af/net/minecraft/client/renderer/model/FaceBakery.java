package net.minecraft.client.renderer.model;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.FaceDirection;
import net.minecraft.client.renderer.Matrix3f;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FaceBakery {
   private static final float SCALE_ROTATION_22_5 = 1.0F / (float)Math.cos((double)((float)Math.PI / 8F)) - 1.0F;
   private static final float SCALE_ROTATION_GENERAL = 1.0F / (float)Math.cos((double)((float)Math.PI / 4F)) - 1.0F;

   public BakedQuad bakeQuad(Vector3f posFrom, Vector3f posTo, BlockPartFace face, TextureAtlasSprite sprite, Direction facing, IModelTransform transformIn, @Nullable BlockPartRotation partRotation, boolean shade, ResourceLocation modelLocationIn) {
      BlockFaceUV blockfaceuv = face.blockFaceUV;
      if (transformIn.isUvLock()) {
         blockfaceuv = updateFaceUV(face.blockFaceUV, facing, transformIn.getRotation(), modelLocationIn);
      }

      float[] afloat = new float[blockfaceuv.uvs.length];
      System.arraycopy(blockfaceuv.uvs, 0, afloat, 0, afloat.length);
      float f = sprite.getUvShrinkRatio();
      float f1 = (blockfaceuv.uvs[0] + blockfaceuv.uvs[0] + blockfaceuv.uvs[2] + blockfaceuv.uvs[2]) / 4.0F;
      float f2 = (blockfaceuv.uvs[1] + blockfaceuv.uvs[1] + blockfaceuv.uvs[3] + blockfaceuv.uvs[3]) / 4.0F;
      blockfaceuv.uvs[0] = MathHelper.lerp(f, blockfaceuv.uvs[0], f1);
      blockfaceuv.uvs[2] = MathHelper.lerp(f, blockfaceuv.uvs[2], f1);
      blockfaceuv.uvs[1] = MathHelper.lerp(f, blockfaceuv.uvs[1], f2);
      blockfaceuv.uvs[3] = MathHelper.lerp(f, blockfaceuv.uvs[3], f2);
      // FORGE: Apply diffuse lighting at render-time instead of baking it in
      int[] aint = this.makeQuadVertexData(blockfaceuv, sprite, facing, this.getPositionsDiv16(posFrom, posTo), transformIn.getRotation(), partRotation, false);
      Direction direction = getFacingFromVertexData(aint);
      System.arraycopy(afloat, 0, blockfaceuv.uvs, 0, afloat.length);
      if (partRotation == null) {
         this.applyFacing(aint, direction);
      }

      net.minecraftforge.client.ForgeHooksClient.fillNormal(aint, direction);
      return new BakedQuad(aint, face.tintIndex, direction, sprite, shade);
   }

   public static BlockFaceUV updateFaceUV(BlockFaceUV blockFaceUVIn, Direction facing, TransformationMatrix modelRotationIn, ResourceLocation modelLocationIn) {
      Matrix4f matrix4f = UVTransformationUtil.getUVLockTransform(modelRotationIn, facing, () -> {
         return "Unable to resolve UVLock for model: " + modelLocationIn;
      }).getMatrix();
      float f = blockFaceUVIn.getVertexU(blockFaceUVIn.getVertexRotatedRev(0));
      float f1 = blockFaceUVIn.getVertexV(blockFaceUVIn.getVertexRotatedRev(0));
      Vector4f vector4f = new Vector4f(f / 16.0F, f1 / 16.0F, 0.0F, 1.0F);
      vector4f.transform(matrix4f);
      float f2 = 16.0F * vector4f.getX();
      float f3 = 16.0F * vector4f.getY();
      float f4 = blockFaceUVIn.getVertexU(blockFaceUVIn.getVertexRotatedRev(2));
      float f5 = blockFaceUVIn.getVertexV(blockFaceUVIn.getVertexRotatedRev(2));
      Vector4f vector4f1 = new Vector4f(f4 / 16.0F, f5 / 16.0F, 0.0F, 1.0F);
      vector4f1.transform(matrix4f);
      float f6 = 16.0F * vector4f1.getX();
      float f7 = 16.0F * vector4f1.getY();
      float f8;
      float f9;
      if (Math.signum(f4 - f) == Math.signum(f6 - f2)) {
         f8 = f2;
         f9 = f6;
      } else {
         f8 = f6;
         f9 = f2;
      }

      float f10;
      float f11;
      if (Math.signum(f5 - f1) == Math.signum(f7 - f3)) {
         f10 = f3;
         f11 = f7;
      } else {
         f10 = f7;
         f11 = f3;
      }

      float f12 = (float)Math.toRadians((double)blockFaceUVIn.rotation);
      Vector3f vector3f = new Vector3f(MathHelper.cos(f12), MathHelper.sin(f12), 0.0F);
      Matrix3f matrix3f = new Matrix3f(matrix4f);
      vector3f.transform(matrix3f);
      int i = Math.floorMod(-((int)Math.round(Math.toDegrees(Math.atan2((double)vector3f.getY(), (double)vector3f.getX())) / 90.0D)) * 90, 360);
      return new BlockFaceUV(new float[]{f8, f10, f9, f11}, i);
   }

   private int[] makeQuadVertexData(BlockFaceUV uvs, TextureAtlasSprite sprite, Direction orientation, float[] posDiv16, TransformationMatrix rotationIn, @Nullable BlockPartRotation partRotation, boolean shade) {
      int[] aint = new int[32];

      for(int i = 0; i < 4; ++i) {
         this.fillVertexData(aint, i, orientation, uvs, posDiv16, sprite, rotationIn, partRotation, shade);
      }

      return aint;
   }

   private int getFaceShadeColor(Direction facing) {
      float f = this.getFaceBrightness(facing);
      int i = MathHelper.clamp((int)(f * 255.0F), 0, 255);
      return -16777216 | i << 16 | i << 8 | i;
   }

   private float getFaceBrightness(Direction facing) {
      switch(facing) {
      case DOWN:
         return 0.5F;
      case UP:
         return 1.0F;
      case NORTH:
      case SOUTH:
         return 0.8F;
      case WEST:
      case EAST:
         return 0.6F;
      default:
         return 1.0F;
      }
   }

   private float[] getPositionsDiv16(Vector3f pos1, Vector3f pos2) {
      float[] afloat = new float[Direction.values().length];
      afloat[FaceDirection.Constants.WEST_INDEX] = pos1.getX() / 16.0F;
      afloat[FaceDirection.Constants.DOWN_INDEX] = pos1.getY() / 16.0F;
      afloat[FaceDirection.Constants.NORTH_INDEX] = pos1.getZ() / 16.0F;
      afloat[FaceDirection.Constants.EAST_INDEX] = pos2.getX() / 16.0F;
      afloat[FaceDirection.Constants.UP_INDEX] = pos2.getY() / 16.0F;
      afloat[FaceDirection.Constants.SOUTH_INDEX] = pos2.getZ() / 16.0F;
      return afloat;
   }

   private void fillVertexData(int[] vertexData, int vertexIndex, Direction facing, BlockFaceUV blockFaceUVIn, float[] posDiv16, TextureAtlasSprite sprite, TransformationMatrix rotationIn, @Nullable BlockPartRotation partRotation, boolean shade) {
      Direction direction = Direction.rotateFace(rotationIn.getMatrix(), facing);
      int i = shade ? this.getFaceShadeColor(direction) : -1;
      FaceDirection.VertexInformation facedirection$vertexinformation = FaceDirection.getFacing(facing).getVertexInformation(vertexIndex);
      Vector3f vector3f = new Vector3f(posDiv16[facedirection$vertexinformation.xIndex], posDiv16[facedirection$vertexinformation.yIndex], posDiv16[facedirection$vertexinformation.zIndex]);
      this.rotatePart(vector3f, partRotation);
      this.rotateVertex(vector3f, rotationIn);
      this.storeVertexData(vertexData, vertexIndex, vector3f, i, sprite, blockFaceUVIn);
   }

   private void storeVertexData(int[] faceData, int storeIndex, Vector3f positionIn, int shadeColor, TextureAtlasSprite sprite, BlockFaceUV faceUV) {
      int i = storeIndex * 8;
      faceData[i] = Float.floatToRawIntBits(positionIn.getX());
      faceData[i + 1] = Float.floatToRawIntBits(positionIn.getY());
      faceData[i + 2] = Float.floatToRawIntBits(positionIn.getZ());
      faceData[i + 3] = shadeColor;
      faceData[i + 4] = Float.floatToRawIntBits(sprite.getInterpolatedU((double)faceUV.getVertexU(storeIndex)  * .999 + faceUV.getVertexU((storeIndex + 2) % 4) * .001));
      faceData[i + 4 + 1] = Float.floatToRawIntBits(sprite.getInterpolatedV((double)faceUV.getVertexV(storeIndex) * .999 + faceUV.getVertexV((storeIndex + 2) % 4) * .001));
   }

   private void rotatePart(Vector3f vec, @Nullable BlockPartRotation partRotation) {
      if (partRotation != null) {
         Vector3f vector3f;
         Vector3f vector3f1;
         switch(partRotation.axis) {
         case X:
            vector3f = new Vector3f(1.0F, 0.0F, 0.0F);
            vector3f1 = new Vector3f(0.0F, 1.0F, 1.0F);
            break;
         case Y:
            vector3f = new Vector3f(0.0F, 1.0F, 0.0F);
            vector3f1 = new Vector3f(1.0F, 0.0F, 1.0F);
            break;
         case Z:
            vector3f = new Vector3f(0.0F, 0.0F, 1.0F);
            vector3f1 = new Vector3f(1.0F, 1.0F, 0.0F);
            break;
         default:
            throw new IllegalArgumentException("There are only 3 axes");
         }

         Quaternion quaternion = new Quaternion(vector3f, partRotation.angle, true);
         if (partRotation.rescale) {
            if (Math.abs(partRotation.angle) == 22.5F) {
               vector3f1.mul(SCALE_ROTATION_22_5);
            } else {
               vector3f1.mul(SCALE_ROTATION_GENERAL);
            }

            vector3f1.add(1.0F, 1.0F, 1.0F);
         } else {
            vector3f1.set(1.0F, 1.0F, 1.0F);
         }

         this.rotateScale(vec, partRotation.origin.copy(), new Matrix4f(quaternion), vector3f1);
      }
   }

   public void rotateVertex(Vector3f posIn, TransformationMatrix transformIn) {
      if (transformIn != TransformationMatrix.identity()) {
         this.rotateScale(posIn, new Vector3f(0.5F, 0.5F, 0.5F), transformIn.getMatrix(), new Vector3f(1.0F, 1.0F, 1.0F));
      }
   }

   private void rotateScale(Vector3f posIn, Vector3f originIn, Matrix4f transformIn, Vector3f scaleIn) {
      Vector4f vector4f = new Vector4f(posIn.getX() - originIn.getX(), posIn.getY() - originIn.getY(), posIn.getZ() - originIn.getZ(), 1.0F);
      vector4f.transform(transformIn);
      vector4f.scale(scaleIn);
      posIn.set(vector4f.getX() + originIn.getX(), vector4f.getY() + originIn.getY(), vector4f.getZ() + originIn.getZ());
   }

   public static Direction getFacingFromVertexData(int[] faceData) {
      Vector3f vector3f = new Vector3f(Float.intBitsToFloat(faceData[0]), Float.intBitsToFloat(faceData[1]), Float.intBitsToFloat(faceData[2]));
      Vector3f vector3f1 = new Vector3f(Float.intBitsToFloat(faceData[8]), Float.intBitsToFloat(faceData[9]), Float.intBitsToFloat(faceData[10]));
      Vector3f vector3f2 = new Vector3f(Float.intBitsToFloat(faceData[16]), Float.intBitsToFloat(faceData[17]), Float.intBitsToFloat(faceData[18]));
      Vector3f vector3f3 = vector3f.copy();
      vector3f3.sub(vector3f1);
      Vector3f vector3f4 = vector3f2.copy();
      vector3f4.sub(vector3f1);
      Vector3f vector3f5 = vector3f4.copy();
      vector3f5.cross(vector3f3);
      vector3f5.normalize();
      Direction direction = null;
      float f = 0.0F;

      for(Direction direction1 : Direction.values()) {
         Vec3i vec3i = direction1.getDirectionVec();
         Vector3f vector3f6 = new Vector3f((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
         float f1 = vector3f5.dot(vector3f6);
         if (f1 >= 0.0F && f1 > f) {
            f = f1;
            direction = direction1;
         }
      }

      return direction == null ? Direction.UP : direction;
   }

   private void applyFacing(int[] vertexData, Direction directionIn) {
      int[] aint = new int[vertexData.length];
      System.arraycopy(vertexData, 0, aint, 0, vertexData.length);
      float[] afloat = new float[Direction.values().length];
      afloat[FaceDirection.Constants.WEST_INDEX] = 999.0F;
      afloat[FaceDirection.Constants.DOWN_INDEX] = 999.0F;
      afloat[FaceDirection.Constants.NORTH_INDEX] = 999.0F;
      afloat[FaceDirection.Constants.EAST_INDEX] = -999.0F;
      afloat[FaceDirection.Constants.UP_INDEX] = -999.0F;
      afloat[FaceDirection.Constants.SOUTH_INDEX] = -999.0F;

      for(int i = 0; i < 4; ++i) {
         int j = 8 * i;
         float f = Float.intBitsToFloat(aint[j]);
         float f1 = Float.intBitsToFloat(aint[j + 1]);
         float f2 = Float.intBitsToFloat(aint[j + 2]);
         if (f < afloat[FaceDirection.Constants.WEST_INDEX]) {
            afloat[FaceDirection.Constants.WEST_INDEX] = f;
         }

         if (f1 < afloat[FaceDirection.Constants.DOWN_INDEX]) {
            afloat[FaceDirection.Constants.DOWN_INDEX] = f1;
         }

         if (f2 < afloat[FaceDirection.Constants.NORTH_INDEX]) {
            afloat[FaceDirection.Constants.NORTH_INDEX] = f2;
         }

         if (f > afloat[FaceDirection.Constants.EAST_INDEX]) {
            afloat[FaceDirection.Constants.EAST_INDEX] = f;
         }

         if (f1 > afloat[FaceDirection.Constants.UP_INDEX]) {
            afloat[FaceDirection.Constants.UP_INDEX] = f1;
         }

         if (f2 > afloat[FaceDirection.Constants.SOUTH_INDEX]) {
            afloat[FaceDirection.Constants.SOUTH_INDEX] = f2;
         }
      }

      FaceDirection facedirection = FaceDirection.getFacing(directionIn);

      for(int i1 = 0; i1 < 4; ++i1) {
         int j1 = 8 * i1;
         FaceDirection.VertexInformation facedirection$vertexinformation = facedirection.getVertexInformation(i1);
         float f8 = afloat[facedirection$vertexinformation.xIndex];
         float f3 = afloat[facedirection$vertexinformation.yIndex];
         float f4 = afloat[facedirection$vertexinformation.zIndex];
         vertexData[j1] = Float.floatToRawIntBits(f8);
         vertexData[j1 + 1] = Float.floatToRawIntBits(f3);
         vertexData[j1 + 2] = Float.floatToRawIntBits(f4);

         for(int k = 0; k < 4; ++k) {
            int l = 8 * k;
            float f5 = Float.intBitsToFloat(aint[l]);
            float f6 = Float.intBitsToFloat(aint[l + 1]);
            float f7 = Float.intBitsToFloat(aint[l + 2]);
            if (MathHelper.epsilonEquals(f8, f5) && MathHelper.epsilonEquals(f3, f6) && MathHelper.epsilonEquals(f4, f7)) {
               vertexData[j1 + 4] = aint[l + 4];
               vertexData[j1 + 4 + 1] = aint[l + 4 + 1];
            }
         }
      }

   }
}