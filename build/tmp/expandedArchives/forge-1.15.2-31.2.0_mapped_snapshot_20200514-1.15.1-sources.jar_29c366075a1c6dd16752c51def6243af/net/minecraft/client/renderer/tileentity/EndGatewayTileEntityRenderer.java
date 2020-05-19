package net.minecraft.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.item.DyeColor;
import net.minecraft.tileentity.EndGatewayTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EndGatewayTileEntityRenderer extends EndPortalTileEntityRenderer<EndGatewayTileEntity> {
   private static final ResourceLocation END_GATEWAY_BEAM_TEXTURE = new ResourceLocation("textures/entity/end_gateway_beam.png");

   public EndGatewayTileEntityRenderer(TileEntityRendererDispatcher p_i226018_1_) {
      super(p_i226018_1_);
   }

   public void render(EndGatewayTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
      if (tileEntityIn.isSpawning() || tileEntityIn.isCoolingDown()) {
         float f = tileEntityIn.isSpawning() ? tileEntityIn.getSpawnPercent(partialTicks) : tileEntityIn.getCooldownPercent(partialTicks);
         double d0 = tileEntityIn.isSpawning() ? 256.0D : 50.0D;
         f = MathHelper.sin(f * (float)Math.PI);
         int i = MathHelper.floor((double)f * d0);
         float[] afloat = tileEntityIn.isSpawning() ? DyeColor.MAGENTA.getColorComponentValues() : DyeColor.PURPLE.getColorComponentValues();
         long j = tileEntityIn.getWorld().getGameTime();
         BeaconTileEntityRenderer.renderBeamSegment(matrixStackIn, bufferIn, END_GATEWAY_BEAM_TEXTURE, partialTicks, f, j, 0, i, afloat, 0.15F, 0.175F);
         BeaconTileEntityRenderer.renderBeamSegment(matrixStackIn, bufferIn, END_GATEWAY_BEAM_TEXTURE, partialTicks, f, j, 0, -i, afloat, 0.15F, 0.175F);
      }

      super.render(tileEntityIn, partialTicks, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
   }

   protected int getPasses(double p_191286_1_) {
      return super.getPasses(p_191286_1_) + 1;
   }

   protected float getOffset() {
      return 1.0F;
   }
}