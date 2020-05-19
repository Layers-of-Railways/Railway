package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix3f;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ExperienceOrbRenderer extends EntityRenderer<ExperienceOrbEntity> {
   private static final ResourceLocation EXPERIENCE_ORB_TEXTURES = new ResourceLocation("textures/entity/experience_orb.png");
   private static final RenderType RENDER_TYPE = RenderType.getEntityTranslucent(EXPERIENCE_ORB_TEXTURES);

   public ExperienceOrbRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn);
      this.shadowSize = 0.15F;
      this.shadowOpaque = 0.75F;
   }

   protected int getBlockLight(ExperienceOrbEntity entityIn, float partialTicks) {
      return MathHelper.clamp(super.getBlockLight(entityIn, partialTicks) + 7, 0, 15);
   }

   public void render(ExperienceOrbEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
      matrixStackIn.push();
      int i = entityIn.getTextureByXP();
      float f = (float)(i % 4 * 16 + 0) / 64.0F;
      float f1 = (float)(i % 4 * 16 + 16) / 64.0F;
      float f2 = (float)(i / 4 * 16 + 0) / 64.0F;
      float f3 = (float)(i / 4 * 16 + 16) / 64.0F;
      float f4 = 1.0F;
      float f5 = 0.5F;
      float f6 = 0.25F;
      float f7 = 255.0F;
      float f8 = ((float)entityIn.xpColor + partialTicks) / 2.0F;
      int j = (int)((MathHelper.sin(f8 + 0.0F) + 1.0F) * 0.5F * 255.0F);
      int k = 255;
      int l = (int)((MathHelper.sin(f8 + 4.1887903F) + 1.0F) * 0.1F * 255.0F);
      matrixStackIn.translate(0.0D, (double)0.1F, 0.0D);
      matrixStackIn.rotate(this.renderManager.getCameraOrientation());
      matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0F));
      float f9 = 0.3F;
      matrixStackIn.scale(0.3F, 0.3F, 0.3F);
      IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RENDER_TYPE);
      MatrixStack.Entry matrixstack$entry = matrixStackIn.getLast();
      Matrix4f matrix4f = matrixstack$entry.getMatrix();
      Matrix3f matrix3f = matrixstack$entry.getNormal();
      vertex(ivertexbuilder, matrix4f, matrix3f, -0.5F, -0.25F, j, 255, l, f, f3, packedLightIn);
      vertex(ivertexbuilder, matrix4f, matrix3f, 0.5F, -0.25F, j, 255, l, f1, f3, packedLightIn);
      vertex(ivertexbuilder, matrix4f, matrix3f, 0.5F, 0.75F, j, 255, l, f1, f2, packedLightIn);
      vertex(ivertexbuilder, matrix4f, matrix3f, -0.5F, 0.75F, j, 255, l, f, f2, packedLightIn);
      matrixStackIn.pop();
      super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
   }

   private static void vertex(IVertexBuilder bufferIn, Matrix4f matrixIn, Matrix3f matrixNormalIn, float x, float y, int red, int green, int blue, float texU, float texV, int packedLight) {
      bufferIn.pos(matrixIn, x, y, 0.0F).color(red, green, blue, 128).tex(texU, texV).overlay(OverlayTexture.NO_OVERLAY).lightmap(packedLight).normal(matrixNormalIn, 0.0F, 1.0F, 0.0F).endVertex();
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(ExperienceOrbEntity entity) {
      return EXPERIENCE_ORB_TEXTURES;
   }
}