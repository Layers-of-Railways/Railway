package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.SnowManModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SnowmanHeadLayer extends LayerRenderer<SnowGolemEntity, SnowManModel<SnowGolemEntity>> {
   public SnowmanHeadLayer(IEntityRenderer<SnowGolemEntity, SnowManModel<SnowGolemEntity>> p_i50922_1_) {
      super(p_i50922_1_);
   }

   public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, SnowGolemEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
      if (!entitylivingbaseIn.isInvisible() && entitylivingbaseIn.isPumpkinEquipped()) {
         matrixStackIn.push();
         this.getEntityModel().func_205070_a().translateRotate(matrixStackIn);
         float f = 0.625F;
         matrixStackIn.translate(0.0D, -0.34375D, 0.0D);
         matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0F));
         matrixStackIn.scale(0.625F, -0.625F, -0.625F);
         ItemStack itemstack = new ItemStack(Blocks.CARVED_PUMPKIN);
         Minecraft.getInstance().getItemRenderer().renderItem(entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.HEAD, false, matrixStackIn, bufferIn, entitylivingbaseIn.world, packedLightIn, LivingRenderer.getPackedOverlay(entitylivingbaseIn, 0.0F));
         matrixStackIn.pop();
      }
   }
}