package net.minecraft.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.CampfireBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.CampfireTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CampfireTileEntityRenderer extends TileEntityRenderer<CampfireTileEntity> {
   public CampfireTileEntityRenderer(TileEntityRendererDispatcher p_i226007_1_) {
      super(p_i226007_1_);
   }

   public void render(CampfireTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
      Direction direction = tileEntityIn.getBlockState().get(CampfireBlock.FACING);
      NonNullList<ItemStack> nonnulllist = tileEntityIn.getInventory();

      for(int i = 0; i < nonnulllist.size(); ++i) {
         ItemStack itemstack = nonnulllist.get(i);
         if (itemstack != ItemStack.EMPTY) {
            matrixStackIn.push();
            matrixStackIn.translate(0.5D, 0.44921875D, 0.5D);
            Direction direction1 = Direction.byHorizontalIndex((i + direction.getHorizontalIndex()) % 4);
            float f = -direction1.getHorizontalAngle();
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(f));
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90.0F));
            matrixStackIn.translate(-0.3125D, -0.3125D, 0.0D);
            matrixStackIn.scale(0.375F, 0.375F, 0.375F);
            Minecraft.getInstance().getItemRenderer().renderItem(itemstack, ItemCameraTransforms.TransformType.FIXED, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn);
            matrixStackIn.pop();
         }
      }

   }
}