package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.FoxModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FoxHeldItemLayer extends LayerRenderer<FoxEntity, FoxModel<FoxEntity>> {
   public FoxHeldItemLayer(IEntityRenderer<FoxEntity, FoxModel<FoxEntity>> p_i50938_1_) {
      super(p_i50938_1_);
   }

   public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, FoxEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
      boolean flag = entitylivingbaseIn.isSleeping();
      boolean flag1 = entitylivingbaseIn.isChild();
      matrixStackIn.push();
      if (flag1) {
         float f = 0.75F;
         matrixStackIn.scale(0.75F, 0.75F, 0.75F);
         matrixStackIn.translate(0.0D, 0.5D, (double)0.209375F);
      }

      matrixStackIn.translate((double)((this.getEntityModel()).head.rotationPointX / 16.0F), (double)((this.getEntityModel()).head.rotationPointY / 16.0F), (double)((this.getEntityModel()).head.rotationPointZ / 16.0F));
      float f1 = entitylivingbaseIn.func_213475_v(partialTicks);
      matrixStackIn.rotate(Vector3f.ZP.rotation(f1));
      matrixStackIn.rotate(Vector3f.YP.rotationDegrees(netHeadYaw));
      matrixStackIn.rotate(Vector3f.XP.rotationDegrees(headPitch));
      if (entitylivingbaseIn.isChild()) {
         if (flag) {
            matrixStackIn.translate((double)0.4F, (double)0.26F, (double)0.15F);
         } else {
            matrixStackIn.translate((double)0.06F, (double)0.26F, -0.5D);
         }
      } else if (flag) {
         matrixStackIn.translate((double)0.46F, (double)0.26F, (double)0.22F);
      } else {
         matrixStackIn.translate((double)0.06F, (double)0.27F, -0.5D);
      }

      matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90.0F));
      if (flag) {
         matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(90.0F));
      }

      ItemStack itemstack = entitylivingbaseIn.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
      Minecraft.getInstance().getFirstPersonRenderer().renderItemSide(entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
      matrixStackIn.pop();
   }
}