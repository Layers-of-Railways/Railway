package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix3f;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.PaintingSpriteUploader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.item.PaintingEntity;
import net.minecraft.entity.item.PaintingType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PaintingRenderer extends EntityRenderer<PaintingEntity> {
   public PaintingRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn);
   }

   public void render(PaintingEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
      matrixStackIn.push();
      matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0F - entityYaw));
      PaintingType paintingtype = entityIn.art;
      float f = 0.0625F;
      matrixStackIn.scale(0.0625F, 0.0625F, 0.0625F);
      IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getEntitySolid(this.getEntityTexture(entityIn)));
      PaintingSpriteUploader paintingspriteuploader = Minecraft.getInstance().getPaintingSpriteUploader();
      this.func_229122_a_(matrixStackIn, ivertexbuilder, entityIn, paintingtype.getWidth(), paintingtype.getHeight(), paintingspriteuploader.getSpriteForPainting(paintingtype), paintingspriteuploader.getBackSprite());
      matrixStackIn.pop();
      super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(PaintingEntity entity) {
      return Minecraft.getInstance().getPaintingSpriteUploader().getBackSprite().getAtlasTexture().getTextureLocation();
   }

   private void func_229122_a_(MatrixStack p_229122_1_, IVertexBuilder p_229122_2_, PaintingEntity p_229122_3_, int p_229122_4_, int p_229122_5_, TextureAtlasSprite p_229122_6_, TextureAtlasSprite p_229122_7_) {
      MatrixStack.Entry matrixstack$entry = p_229122_1_.getLast();
      Matrix4f matrix4f = matrixstack$entry.getMatrix();
      Matrix3f matrix3f = matrixstack$entry.getNormal();
      float f = (float)(-p_229122_4_) / 2.0F;
      float f1 = (float)(-p_229122_5_) / 2.0F;
      float f2 = 0.5F;
      float f3 = p_229122_7_.getMinU();
      float f4 = p_229122_7_.getMaxU();
      float f5 = p_229122_7_.getMinV();
      float f6 = p_229122_7_.getMaxV();
      float f7 = p_229122_7_.getMinU();
      float f8 = p_229122_7_.getMaxU();
      float f9 = p_229122_7_.getMinV();
      float f10 = p_229122_7_.getInterpolatedV(1.0D);
      float f11 = p_229122_7_.getMinU();
      float f12 = p_229122_7_.getInterpolatedU(1.0D);
      float f13 = p_229122_7_.getMinV();
      float f14 = p_229122_7_.getMaxV();
      int i = p_229122_4_ / 16;
      int j = p_229122_5_ / 16;
      double d0 = 16.0D / (double)i;
      double d1 = 16.0D / (double)j;

      for(int k = 0; k < i; ++k) {
         for(int l = 0; l < j; ++l) {
            float f15 = f + (float)((k + 1) * 16);
            float f16 = f + (float)(k * 16);
            float f17 = f1 + (float)((l + 1) * 16);
            float f18 = f1 + (float)(l * 16);
            int i1 = MathHelper.floor(p_229122_3_.getPosX());
            int j1 = MathHelper.floor(p_229122_3_.getPosY() + (double)((f17 + f18) / 2.0F / 16.0F));
            int k1 = MathHelper.floor(p_229122_3_.getPosZ());
            Direction direction = p_229122_3_.getHorizontalFacing();
            if (direction == Direction.NORTH) {
               i1 = MathHelper.floor(p_229122_3_.getPosX() + (double)((f15 + f16) / 2.0F / 16.0F));
            }

            if (direction == Direction.WEST) {
               k1 = MathHelper.floor(p_229122_3_.getPosZ() - (double)((f15 + f16) / 2.0F / 16.0F));
            }

            if (direction == Direction.SOUTH) {
               i1 = MathHelper.floor(p_229122_3_.getPosX() - (double)((f15 + f16) / 2.0F / 16.0F));
            }

            if (direction == Direction.EAST) {
               k1 = MathHelper.floor(p_229122_3_.getPosZ() + (double)((f15 + f16) / 2.0F / 16.0F));
            }

            int l1 = WorldRenderer.getCombinedLight(p_229122_3_.world, new BlockPos(i1, j1, k1));
            float f19 = p_229122_6_.getInterpolatedU(d0 * (double)(i - k));
            float f20 = p_229122_6_.getInterpolatedU(d0 * (double)(i - (k + 1)));
            float f21 = p_229122_6_.getInterpolatedV(d1 * (double)(j - l));
            float f22 = p_229122_6_.getInterpolatedV(d1 * (double)(j - (l + 1)));
            this.func_229121_a_(matrix4f, matrix3f, p_229122_2_, f15, f18, f20, f21, -0.5F, 0, 0, -1, l1);
            this.func_229121_a_(matrix4f, matrix3f, p_229122_2_, f16, f18, f19, f21, -0.5F, 0, 0, -1, l1);
            this.func_229121_a_(matrix4f, matrix3f, p_229122_2_, f16, f17, f19, f22, -0.5F, 0, 0, -1, l1);
            this.func_229121_a_(matrix4f, matrix3f, p_229122_2_, f15, f17, f20, f22, -0.5F, 0, 0, -1, l1);
            this.func_229121_a_(matrix4f, matrix3f, p_229122_2_, f15, f17, f3, f5, 0.5F, 0, 0, 1, l1);
            this.func_229121_a_(matrix4f, matrix3f, p_229122_2_, f16, f17, f4, f5, 0.5F, 0, 0, 1, l1);
            this.func_229121_a_(matrix4f, matrix3f, p_229122_2_, f16, f18, f4, f6, 0.5F, 0, 0, 1, l1);
            this.func_229121_a_(matrix4f, matrix3f, p_229122_2_, f15, f18, f3, f6, 0.5F, 0, 0, 1, l1);
            this.func_229121_a_(matrix4f, matrix3f, p_229122_2_, f15, f17, f7, f9, -0.5F, 0, 1, 0, l1);
            this.func_229121_a_(matrix4f, matrix3f, p_229122_2_, f16, f17, f8, f9, -0.5F, 0, 1, 0, l1);
            this.func_229121_a_(matrix4f, matrix3f, p_229122_2_, f16, f17, f8, f10, 0.5F, 0, 1, 0, l1);
            this.func_229121_a_(matrix4f, matrix3f, p_229122_2_, f15, f17, f7, f10, 0.5F, 0, 1, 0, l1);
            this.func_229121_a_(matrix4f, matrix3f, p_229122_2_, f15, f18, f7, f9, 0.5F, 0, -1, 0, l1);
            this.func_229121_a_(matrix4f, matrix3f, p_229122_2_, f16, f18, f8, f9, 0.5F, 0, -1, 0, l1);
            this.func_229121_a_(matrix4f, matrix3f, p_229122_2_, f16, f18, f8, f10, -0.5F, 0, -1, 0, l1);
            this.func_229121_a_(matrix4f, matrix3f, p_229122_2_, f15, f18, f7, f10, -0.5F, 0, -1, 0, l1);
            this.func_229121_a_(matrix4f, matrix3f, p_229122_2_, f15, f17, f12, f13, 0.5F, -1, 0, 0, l1);
            this.func_229121_a_(matrix4f, matrix3f, p_229122_2_, f15, f18, f12, f14, 0.5F, -1, 0, 0, l1);
            this.func_229121_a_(matrix4f, matrix3f, p_229122_2_, f15, f18, f11, f14, -0.5F, -1, 0, 0, l1);
            this.func_229121_a_(matrix4f, matrix3f, p_229122_2_, f15, f17, f11, f13, -0.5F, -1, 0, 0, l1);
            this.func_229121_a_(matrix4f, matrix3f, p_229122_2_, f16, f17, f12, f13, -0.5F, 1, 0, 0, l1);
            this.func_229121_a_(matrix4f, matrix3f, p_229122_2_, f16, f18, f12, f14, -0.5F, 1, 0, 0, l1);
            this.func_229121_a_(matrix4f, matrix3f, p_229122_2_, f16, f18, f11, f14, 0.5F, 1, 0, 0, l1);
            this.func_229121_a_(matrix4f, matrix3f, p_229122_2_, f16, f17, f11, f13, 0.5F, 1, 0, 0, l1);
         }
      }

   }

   private void func_229121_a_(Matrix4f p_229121_1_, Matrix3f p_229121_2_, IVertexBuilder p_229121_3_, float p_229121_4_, float p_229121_5_, float p_229121_6_, float p_229121_7_, float p_229121_8_, int p_229121_9_, int p_229121_10_, int p_229121_11_, int p_229121_12_) {
      p_229121_3_.pos(p_229121_1_, p_229121_4_, p_229121_5_, p_229121_8_).color(255, 255, 255, 255).tex(p_229121_6_, p_229121_7_).overlay(OverlayTexture.NO_OVERLAY).lightmap(p_229121_12_).normal(p_229121_2_, (float)p_229121_9_, (float)p_229121_10_, (float)p_229121_11_).endVertex();
   }
}