package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Deadmau5HeadLayer extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {
   public Deadmau5HeadLayer(IEntityRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> p_i50945_1_) {
      super(p_i50945_1_);
   }

   public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, AbstractClientPlayerEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
      if ("deadmau5".equals(entitylivingbaseIn.getName().getString()) && entitylivingbaseIn.hasSkin() && !entitylivingbaseIn.isInvisible()) {
         IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getEntitySolid(entitylivingbaseIn.getLocationSkin()));
         int i = LivingRenderer.getPackedOverlay(entitylivingbaseIn, 0.0F);

         for(int j = 0; j < 2; ++j) {
            float f = MathHelper.lerp(partialTicks, entitylivingbaseIn.prevRotationYaw, entitylivingbaseIn.rotationYaw) - MathHelper.lerp(partialTicks, entitylivingbaseIn.prevRenderYawOffset, entitylivingbaseIn.renderYawOffset);
            float f1 = MathHelper.lerp(partialTicks, entitylivingbaseIn.prevRotationPitch, entitylivingbaseIn.rotationPitch);
            matrixStackIn.push();
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(f));
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(f1));
            matrixStackIn.translate((double)(0.375F * (float)(j * 2 - 1)), 0.0D, 0.0D);
            matrixStackIn.translate(0.0D, -0.375D, 0.0D);
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-f1));
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-f));
            float f2 = 1.3333334F;
            matrixStackIn.scale(1.3333334F, 1.3333334F, 1.3333334F);
            this.getEntityModel().renderEars(matrixStackIn, ivertexbuilder, packedLightIn, i);
            matrixStackIn.pop();
         }

      }
   }
}