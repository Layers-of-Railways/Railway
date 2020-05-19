package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.DolphinModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DolphinCarriedItemLayer extends LayerRenderer<DolphinEntity, DolphinModel<DolphinEntity>> {
   public DolphinCarriedItemLayer(IEntityRenderer<DolphinEntity, DolphinModel<DolphinEntity>> p_i50944_1_) {
      super(p_i50944_1_);
   }

   public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, DolphinEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
      boolean flag = entitylivingbaseIn.getPrimaryHand() == HandSide.RIGHT;
      matrixStackIn.push();
      float f = 1.0F;
      float f1 = -1.0F;
      float f2 = MathHelper.abs(entitylivingbaseIn.rotationPitch) / 60.0F;
      if (entitylivingbaseIn.rotationPitch < 0.0F) {
         matrixStackIn.translate(0.0D, (double)(1.0F - f2 * 0.5F), (double)(-1.0F + f2 * 0.5F));
      } else {
         matrixStackIn.translate(0.0D, (double)(1.0F + f2 * 0.8F), (double)(-1.0F + f2 * 0.2F));
      }

      ItemStack itemstack = flag ? entitylivingbaseIn.getHeldItemMainhand() : entitylivingbaseIn.getHeldItemOffhand();
      Minecraft.getInstance().getFirstPersonRenderer().renderItemSide(entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
      matrixStackIn.pop();
   }
}