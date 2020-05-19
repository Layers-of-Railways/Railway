package net.minecraft.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.tileentity.BellTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BellTileEntityRenderer extends TileEntityRenderer<BellTileEntity> {
   public static final Material BELL_BODY_TEXTURE = new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("entity/bell/bell_body"));
   private final ModelRenderer modelRenderer = new ModelRenderer(32, 32, 0, 0);

   public BellTileEntityRenderer(TileEntityRendererDispatcher p_i226005_1_) {
      super(p_i226005_1_);
      this.modelRenderer.addBox(-3.0F, -6.0F, -3.0F, 6.0F, 7.0F, 6.0F);
      this.modelRenderer.setRotationPoint(8.0F, 12.0F, 8.0F);
      ModelRenderer modelrenderer = new ModelRenderer(32, 32, 0, 13);
      modelrenderer.addBox(4.0F, 4.0F, 4.0F, 8.0F, 2.0F, 8.0F);
      modelrenderer.setRotationPoint(-8.0F, -12.0F, -8.0F);
      this.modelRenderer.addChild(modelrenderer);
   }

   public void render(BellTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
      float f = (float)tileEntityIn.ringingTicks + partialTicks;
      float f1 = 0.0F;
      float f2 = 0.0F;
      if (tileEntityIn.isRinging) {
         float f3 = MathHelper.sin(f / (float)Math.PI) / (4.0F + f / 3.0F);
         if (tileEntityIn.ringDirection == Direction.NORTH) {
            f1 = -f3;
         } else if (tileEntityIn.ringDirection == Direction.SOUTH) {
            f1 = f3;
         } else if (tileEntityIn.ringDirection == Direction.EAST) {
            f2 = -f3;
         } else if (tileEntityIn.ringDirection == Direction.WEST) {
            f2 = f3;
         }
      }

      this.modelRenderer.rotateAngleX = f1;
      this.modelRenderer.rotateAngleZ = f2;
      IVertexBuilder ivertexbuilder = BELL_BODY_TEXTURE.getBuffer(bufferIn, RenderType::getEntitySolid);
      this.modelRenderer.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn);
   }
}