package net.minecraft.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.LecternBlock;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.model.BookModel;
import net.minecraft.tileentity.LecternTileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LecternTileEntityRenderer extends TileEntityRenderer<LecternTileEntity> {
   private final BookModel field_217656_d = new BookModel();

   public LecternTileEntityRenderer(TileEntityRendererDispatcher p_i226011_1_) {
      super(p_i226011_1_);
   }

   public void render(LecternTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
      BlockState blockstate = tileEntityIn.getBlockState();
      if (blockstate.get(LecternBlock.HAS_BOOK)) {
         matrixStackIn.push();
         matrixStackIn.translate(0.5D, 1.0625D, 0.5D);
         float f = blockstate.get(LecternBlock.FACING).rotateY().getHorizontalAngle();
         matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-f));
         matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(67.5F));
         matrixStackIn.translate(0.0D, -0.125D, 0.0D);
         this.field_217656_d.func_228247_a_(0.0F, 0.1F, 0.9F, 1.2F);
         IVertexBuilder ivertexbuilder = EnchantmentTableTileEntityRenderer.TEXTURE_BOOK.getBuffer(bufferIn, RenderType::getEntitySolid);
         this.field_217656_d.func_228249_b_(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
         matrixStackIn.pop();
      }
   }
}