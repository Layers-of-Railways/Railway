package net.minecraft.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.model.ShulkerModel;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.item.DyeColor;
import net.minecraft.tileentity.ShulkerBoxTileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ShulkerBoxTileEntityRenderer extends TileEntityRenderer<ShulkerBoxTileEntity> {
   private final ShulkerModel<?> model;

   public ShulkerBoxTileEntityRenderer(ShulkerModel<?> p_i226013_1_, TileEntityRendererDispatcher p_i226013_2_) {
      super(p_i226013_2_);
      this.model = p_i226013_1_;
   }

   public void render(ShulkerBoxTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
      Direction direction = Direction.UP;
      if (tileEntityIn.hasWorld()) {
         BlockState blockstate = tileEntityIn.getWorld().getBlockState(tileEntityIn.getPos());
         if (blockstate.getBlock() instanceof ShulkerBoxBlock) {
            direction = blockstate.get(ShulkerBoxBlock.FACING);
         }
      }

      DyeColor dyecolor = tileEntityIn.getColor();
      Material material;
      if (dyecolor == null) {
         material = Atlases.DEFAULT_SHULKER_TEXTURE;
      } else {
         material = Atlases.SHULKER_TEXTURES.get(dyecolor.getId());
      }

      matrixStackIn.push();
      matrixStackIn.translate(0.5D, 0.5D, 0.5D);
      float f = 0.9995F;
      matrixStackIn.scale(0.9995F, 0.9995F, 0.9995F);
      matrixStackIn.rotate(direction.getRotation());
      matrixStackIn.scale(1.0F, -1.0F, -1.0F);
      matrixStackIn.translate(0.0D, -1.0D, 0.0D);
      IVertexBuilder ivertexbuilder = material.getBuffer(bufferIn, RenderType::getEntityCutoutNoCull);
      this.model.getBase().render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn);
      matrixStackIn.translate(0.0D, (double)(-tileEntityIn.getProgress(partialTicks) * 0.5F), 0.0D);
      matrixStackIn.rotate(Vector3f.YP.rotationDegrees(270.0F * tileEntityIn.getProgress(partialTicks)));
      this.model.getLid().render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn);
      matrixStackIn.pop();
   }
}