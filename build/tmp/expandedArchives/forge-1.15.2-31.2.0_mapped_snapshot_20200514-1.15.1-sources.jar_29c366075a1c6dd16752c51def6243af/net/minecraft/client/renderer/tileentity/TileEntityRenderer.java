package net.minecraft.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class TileEntityRenderer<T extends TileEntity> {
   protected final TileEntityRendererDispatcher renderDispatcher;

   public TileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
      this.renderDispatcher = rendererDispatcherIn;
   }

   public abstract void render(T tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn);

   public boolean isGlobalRenderer(T te) {
      return false;
   }
}