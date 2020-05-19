package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.item.minecart.TNTMinecartEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TNTMinecartRenderer extends MinecartRenderer<TNTMinecartEntity> {
   public TNTMinecartRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn);
   }

   protected void renderBlockState(TNTMinecartEntity entityIn, float partialTicks, BlockState stateIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
      int i = entityIn.getFuseTicks();
      if (i > -1 && (float)i - partialTicks + 1.0F < 10.0F) {
         float f = 1.0F - ((float)i - partialTicks + 1.0F) / 10.0F;
         f = MathHelper.clamp(f, 0.0F, 1.0F);
         f = f * f;
         f = f * f;
         float f1 = 1.0F + f * 0.3F;
         matrixStackIn.scale(f1, f1, f1);
      }

      renderTntFlash(stateIn, matrixStackIn, bufferIn, packedLightIn, i > -1 && i / 5 % 2 == 0);
   }

   public static void renderTntFlash(BlockState blockStateIn, MatrixStack matrixStackIn, IRenderTypeBuffer renderTypeBuffer, int combinedLight, boolean doFullBright) {
      int i;
      if (doFullBright) {
         i = OverlayTexture.getPackedUV(OverlayTexture.getU(1.0F), 10);
      } else {
         i = OverlayTexture.NO_OVERLAY;
      }

      Minecraft.getInstance().getBlockRendererDispatcher().renderBlock(blockStateIn, matrixStackIn, renderTypeBuffer, combinedLight, i);
   }
}