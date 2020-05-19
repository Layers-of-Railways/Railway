package net.minecraft.client.renderer.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.Random;
import net.minecraft.client.renderer.Matrix3f;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelRenderer {
   private float textureWidth = 64.0F;
   private float textureHeight = 32.0F;
   private int textureOffsetX;
   private int textureOffsetY;
   public float rotationPointX;
   public float rotationPointY;
   public float rotationPointZ;
   public float rotateAngleX;
   public float rotateAngleY;
   public float rotateAngleZ;
   public boolean mirror;
   public boolean showModel = true;
   private final ObjectList<ModelRenderer.ModelBox> cubeList = new ObjectArrayList<>();
   private final ObjectList<ModelRenderer> childModels = new ObjectArrayList<>();

   public ModelRenderer(Model model) {
      model.accept(this);
      this.setTextureSize(model.textureWidth, model.textureHeight);
   }

   public ModelRenderer(Model model, int texOffX, int texOffY) {
      this(model.textureWidth, model.textureHeight, texOffX, texOffY);
      model.accept(this);
   }

   public ModelRenderer(int textureWidthIn, int textureHeightIn, int textureOffsetXIn, int textureOffsetYIn) {
      this.setTextureSize(textureWidthIn, textureHeightIn);
      this.setTextureOffset(textureOffsetXIn, textureOffsetYIn);
   }

   public void copyModelAngles(ModelRenderer modelRendererIn) {
      this.rotateAngleX = modelRendererIn.rotateAngleX;
      this.rotateAngleY = modelRendererIn.rotateAngleY;
      this.rotateAngleZ = modelRendererIn.rotateAngleZ;
      this.rotationPointX = modelRendererIn.rotationPointX;
      this.rotationPointY = modelRendererIn.rotationPointY;
      this.rotationPointZ = modelRendererIn.rotationPointZ;
   }

   /**
    * Sets the current box's rotation points and rotation angles to another box.
    */
   public void addChild(ModelRenderer renderer) {
      this.childModels.add(renderer);
   }

   public ModelRenderer setTextureOffset(int x, int y) {
      this.textureOffsetX = x;
      this.textureOffsetY = y;
      return this;
   }

   public ModelRenderer addBox(String partName, float x, float y, float z, int width, int height, int depth, float delta, int texX, int texY) {
      this.setTextureOffset(texX, texY);
      this.addBox(this.textureOffsetX, this.textureOffsetY, x, y, z, (float)width, (float)height, (float)depth, delta, delta, delta, this.mirror, false);
      return this;
   }

   public ModelRenderer addBox(float x, float y, float z, float width, float height, float depth) {
      this.addBox(this.textureOffsetX, this.textureOffsetY, x, y, z, width, height, depth, 0.0F, 0.0F, 0.0F, this.mirror, false);
      return this;
   }

   public ModelRenderer addBox(float x, float y, float z, float width, float height, float depth, boolean mirrorIn) {
      this.addBox(this.textureOffsetX, this.textureOffsetY, x, y, z, width, height, depth, 0.0F, 0.0F, 0.0F, mirrorIn, false);
      return this;
   }

   public void addBox(float x, float y, float z, float width, float height, float depth, float delta) {
      this.addBox(this.textureOffsetX, this.textureOffsetY, x, y, z, width, height, depth, delta, delta, delta, this.mirror, false);
   }

   public void addBox(float x, float y, float z, float width, float height, float depth, float deltaX, float deltaY, float deltaZ) {
      this.addBox(this.textureOffsetX, this.textureOffsetY, x, y, z, width, height, depth, deltaX, deltaY, deltaZ, this.mirror, false);
   }

   public void addBox(float x, float y, float z, float width, float height, float depth, float delta, boolean mirrorIn) {
      this.addBox(this.textureOffsetX, this.textureOffsetY, x, y, z, width, height, depth, delta, delta, delta, mirrorIn, false);
   }

   private void addBox(int texOffX, int texOffY, float x, float y, float z, float width, float height, float depth, float deltaX, float deltaY, float deltaZ, boolean mirorIn, boolean p_228305_13_) {
      this.cubeList.add(new ModelRenderer.ModelBox(texOffX, texOffY, x, y, z, width, height, depth, deltaX, deltaY, deltaZ, mirorIn, this.textureWidth, this.textureHeight));
   }

   public void setRotationPoint(float rotationPointXIn, float rotationPointYIn, float rotationPointZIn) {
      this.rotationPointX = rotationPointXIn;
      this.rotationPointY = rotationPointYIn;
      this.rotationPointZ = rotationPointZIn;
   }

   public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn) {
      this.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
   }

   public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
      if (this.showModel) {
         if (!this.cubeList.isEmpty() || !this.childModels.isEmpty()) {
            matrixStackIn.push();
            this.translateRotate(matrixStackIn);
            this.doRender(matrixStackIn.getLast(), bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

            for(ModelRenderer modelrenderer : this.childModels) {
               modelrenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            }

            matrixStackIn.pop();
         }
      }
   }

   public void translateRotate(MatrixStack matrixStackIn) {
      matrixStackIn.translate((double)(this.rotationPointX / 16.0F), (double)(this.rotationPointY / 16.0F), (double)(this.rotationPointZ / 16.0F));
      if (this.rotateAngleZ != 0.0F) {
         matrixStackIn.rotate(Vector3f.ZP.rotation(this.rotateAngleZ));
      }

      if (this.rotateAngleY != 0.0F) {
         matrixStackIn.rotate(Vector3f.YP.rotation(this.rotateAngleY));
      }

      if (this.rotateAngleX != 0.0F) {
         matrixStackIn.rotate(Vector3f.XP.rotation(this.rotateAngleX));
      }

   }

   private void doRender(MatrixStack.Entry matrixEntryIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
      Matrix4f matrix4f = matrixEntryIn.getMatrix();
      Matrix3f matrix3f = matrixEntryIn.getNormal();

      for(ModelRenderer.ModelBox modelrenderer$modelbox : this.cubeList) {
         for(ModelRenderer.TexturedQuad modelrenderer$texturedquad : modelrenderer$modelbox.quads) {
            Vector3f vector3f = modelrenderer$texturedquad.normal.copy();
            vector3f.transform(matrix3f);
            float f = vector3f.getX();
            float f1 = vector3f.getY();
            float f2 = vector3f.getZ();

            for(int i = 0; i < 4; ++i) {
               ModelRenderer.PositionTextureVertex modelrenderer$positiontexturevertex = modelrenderer$texturedquad.vertexPositions[i];
               float f3 = modelrenderer$positiontexturevertex.position.getX() / 16.0F;
               float f4 = modelrenderer$positiontexturevertex.position.getY() / 16.0F;
               float f5 = modelrenderer$positiontexturevertex.position.getZ() / 16.0F;
               Vector4f vector4f = new Vector4f(f3, f4, f5, 1.0F);
               vector4f.transform(matrix4f);
               bufferIn.addVertex(vector4f.getX(), vector4f.getY(), vector4f.getZ(), red, green, blue, alpha, modelrenderer$positiontexturevertex.textureU, modelrenderer$positiontexturevertex.textureV, packedOverlayIn, packedLightIn, f, f1, f2);
            }
         }
      }

   }

   /**
    * Returns the model renderer with the new texture parameters.
    */
   public ModelRenderer setTextureSize(int textureWidthIn, int textureHeightIn) {
      this.textureWidth = (float)textureWidthIn;
      this.textureHeight = (float)textureHeightIn;
      return this;
   }

   public ModelRenderer.ModelBox getRandomCube(Random randomIn) {
      return this.cubeList.get(randomIn.nextInt(this.cubeList.size()));
   }

   @OnlyIn(Dist.CLIENT)
   public static class ModelBox {
      private final ModelRenderer.TexturedQuad[] quads;
      public final float posX1;
      public final float posY1;
      public final float posZ1;
      public final float posX2;
      public final float posY2;
      public final float posZ2;

      public ModelBox(int texOffX, int texOffY, float x, float y, float z, float width, float height, float depth, float deltaX, float deltaY, float deltaZ, boolean mirorIn, float texWidth, float texHeight) {
         this.posX1 = x;
         this.posY1 = y;
         this.posZ1 = z;
         this.posX2 = x + width;
         this.posY2 = y + height;
         this.posZ2 = z + depth;
         this.quads = new ModelRenderer.TexturedQuad[6];
         float f = x + width;
         float f1 = y + height;
         float f2 = z + depth;
         x = x - deltaX;
         y = y - deltaY;
         z = z - deltaZ;
         f = f + deltaX;
         f1 = f1 + deltaY;
         f2 = f2 + deltaZ;
         if (mirorIn) {
            float f3 = f;
            f = x;
            x = f3;
         }

         ModelRenderer.PositionTextureVertex modelrenderer$positiontexturevertex7 = new ModelRenderer.PositionTextureVertex(x, y, z, 0.0F, 0.0F);
         ModelRenderer.PositionTextureVertex modelrenderer$positiontexturevertex = new ModelRenderer.PositionTextureVertex(f, y, z, 0.0F, 8.0F);
         ModelRenderer.PositionTextureVertex modelrenderer$positiontexturevertex1 = new ModelRenderer.PositionTextureVertex(f, f1, z, 8.0F, 8.0F);
         ModelRenderer.PositionTextureVertex modelrenderer$positiontexturevertex2 = new ModelRenderer.PositionTextureVertex(x, f1, z, 8.0F, 0.0F);
         ModelRenderer.PositionTextureVertex modelrenderer$positiontexturevertex3 = new ModelRenderer.PositionTextureVertex(x, y, f2, 0.0F, 0.0F);
         ModelRenderer.PositionTextureVertex modelrenderer$positiontexturevertex4 = new ModelRenderer.PositionTextureVertex(f, y, f2, 0.0F, 8.0F);
         ModelRenderer.PositionTextureVertex modelrenderer$positiontexturevertex5 = new ModelRenderer.PositionTextureVertex(f, f1, f2, 8.0F, 8.0F);
         ModelRenderer.PositionTextureVertex modelrenderer$positiontexturevertex6 = new ModelRenderer.PositionTextureVertex(x, f1, f2, 8.0F, 0.0F);
         float f4 = (float)texOffX;
         float f5 = (float)texOffX + depth;
         float f6 = (float)texOffX + depth + width;
         float f7 = (float)texOffX + depth + width + width;
         float f8 = (float)texOffX + depth + width + depth;
         float f9 = (float)texOffX + depth + width + depth + width;
         float f10 = (float)texOffY;
         float f11 = (float)texOffY + depth;
         float f12 = (float)texOffY + depth + height;
         this.quads[2] = new ModelRenderer.TexturedQuad(new ModelRenderer.PositionTextureVertex[]{modelrenderer$positiontexturevertex4, modelrenderer$positiontexturevertex3, modelrenderer$positiontexturevertex7, modelrenderer$positiontexturevertex}, f5, f10, f6, f11, texWidth, texHeight, mirorIn, Direction.DOWN);
         this.quads[3] = new ModelRenderer.TexturedQuad(new ModelRenderer.PositionTextureVertex[]{modelrenderer$positiontexturevertex1, modelrenderer$positiontexturevertex2, modelrenderer$positiontexturevertex6, modelrenderer$positiontexturevertex5}, f6, f11, f7, f10, texWidth, texHeight, mirorIn, Direction.UP);
         this.quads[1] = new ModelRenderer.TexturedQuad(new ModelRenderer.PositionTextureVertex[]{modelrenderer$positiontexturevertex7, modelrenderer$positiontexturevertex3, modelrenderer$positiontexturevertex6, modelrenderer$positiontexturevertex2}, f4, f11, f5, f12, texWidth, texHeight, mirorIn, Direction.WEST);
         this.quads[4] = new ModelRenderer.TexturedQuad(new ModelRenderer.PositionTextureVertex[]{modelrenderer$positiontexturevertex, modelrenderer$positiontexturevertex7, modelrenderer$positiontexturevertex2, modelrenderer$positiontexturevertex1}, f5, f11, f6, f12, texWidth, texHeight, mirorIn, Direction.NORTH);
         this.quads[0] = new ModelRenderer.TexturedQuad(new ModelRenderer.PositionTextureVertex[]{modelrenderer$positiontexturevertex4, modelrenderer$positiontexturevertex, modelrenderer$positiontexturevertex1, modelrenderer$positiontexturevertex5}, f6, f11, f8, f12, texWidth, texHeight, mirorIn, Direction.EAST);
         this.quads[5] = new ModelRenderer.TexturedQuad(new ModelRenderer.PositionTextureVertex[]{modelrenderer$positiontexturevertex3, modelrenderer$positiontexturevertex4, modelrenderer$positiontexturevertex5, modelrenderer$positiontexturevertex6}, f8, f11, f9, f12, texWidth, texHeight, mirorIn, Direction.SOUTH);
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class PositionTextureVertex {
      public final Vector3f position;
      public final float textureU;
      public final float textureV;

      public PositionTextureVertex(float x, float y, float z, float texU, float texV) {
         this(new Vector3f(x, y, z), texU, texV);
      }

      public ModelRenderer.PositionTextureVertex setTextureUV(float texU, float texV) {
         return new ModelRenderer.PositionTextureVertex(this.position, texU, texV);
      }

      public PositionTextureVertex(Vector3f posIn, float texU, float texV) {
         this.position = posIn;
         this.textureU = texU;
         this.textureV = texV;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class TexturedQuad {
      public final ModelRenderer.PositionTextureVertex[] vertexPositions;
      public final Vector3f normal;

      public TexturedQuad(ModelRenderer.PositionTextureVertex[] positionsIn, float u1, float v1, float u2, float v2, float texWidth, float texHeight, boolean mirrorIn, Direction directionIn) {
         this.vertexPositions = positionsIn;
         float f = 0.0F / texWidth;
         float f1 = 0.0F / texHeight;
         positionsIn[0] = positionsIn[0].setTextureUV(u2 / texWidth - f, v1 / texHeight + f1);
         positionsIn[1] = positionsIn[1].setTextureUV(u1 / texWidth + f, v1 / texHeight + f1);
         positionsIn[2] = positionsIn[2].setTextureUV(u1 / texWidth + f, v2 / texHeight - f1);
         positionsIn[3] = positionsIn[3].setTextureUV(u2 / texWidth - f, v2 / texHeight - f1);
         if (mirrorIn) {
            int i = positionsIn.length;

            for(int j = 0; j < i / 2; ++j) {
               ModelRenderer.PositionTextureVertex modelrenderer$positiontexturevertex = positionsIn[j];
               positionsIn[j] = positionsIn[i - 1 - j];
               positionsIn[i - 1 - j] = modelrenderer$positiontexturevertex;
            }
         }

         this.normal = directionIn.toVector3f();
         if (mirrorIn) {
            this.normal.mul(-1.0F, 1.0F, 1.0F);
         }

      }
   }
}