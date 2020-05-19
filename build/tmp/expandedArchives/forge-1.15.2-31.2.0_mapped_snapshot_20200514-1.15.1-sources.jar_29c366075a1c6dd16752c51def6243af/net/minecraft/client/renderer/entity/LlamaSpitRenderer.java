package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.model.LlamaSpitModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LlamaSpitRenderer extends EntityRenderer<LlamaSpitEntity> {
   private static final ResourceLocation LLAMA_SPIT_TEXTURE = new ResourceLocation("textures/entity/llama/spit.png");
   private final LlamaSpitModel<LlamaSpitEntity> model = new LlamaSpitModel<>();

   public LlamaSpitRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn);
   }

   public void render(LlamaSpitEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
      matrixStackIn.push();
      matrixStackIn.translate(0.0D, (double)0.15F, 0.0D);
      matrixStackIn.rotate(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.prevRotationYaw, entityIn.rotationYaw) - 90.0F));
      matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.prevRotationPitch, entityIn.rotationPitch)));
      this.model.setRotationAngles(entityIn, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F);
      IVertexBuilder ivertexbuilder = bufferIn.getBuffer(this.model.getRenderType(LLAMA_SPIT_TEXTURE));
      this.model.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      matrixStackIn.pop();
      super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(LlamaSpitEntity entity) {
      return LLAMA_SPIT_TEXTURE;
   }
}