package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.IronGolemModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IronGolenFlowerLayer extends LayerRenderer<IronGolemEntity, IronGolemModel<IronGolemEntity>> {
   public IronGolenFlowerLayer(IEntityRenderer<IronGolemEntity, IronGolemModel<IronGolemEntity>> p_i50935_1_) {
      super(p_i50935_1_);
   }

   public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, IronGolemEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
      if (entitylivingbaseIn.getHoldRoseTick() != 0) {
         matrixStackIn.push();
         ModelRenderer modelrenderer = this.getEntityModel().getArmHoldingRose();
         modelrenderer.translateRotate(matrixStackIn);
         matrixStackIn.translate(-1.1875D, 1.0625D, -0.9375D);
         matrixStackIn.translate(0.5D, 0.5D, 0.5D);
         float f = 0.5F;
         matrixStackIn.scale(0.5F, 0.5F, 0.5F);
         matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-90.0F));
         matrixStackIn.translate(-0.5D, -0.5D, -0.5D);
         Minecraft.getInstance().getBlockRendererDispatcher().renderBlock(Blocks.POPPY.getDefaultState(), matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY);
         matrixStackIn.pop();
      }
   }
}