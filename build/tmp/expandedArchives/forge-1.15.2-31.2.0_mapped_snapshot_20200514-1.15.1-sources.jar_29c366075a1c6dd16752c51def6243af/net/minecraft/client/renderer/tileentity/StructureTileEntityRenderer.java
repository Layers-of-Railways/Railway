package net.minecraft.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.state.properties.StructureMode;
import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StructureTileEntityRenderer extends TileEntityRenderer<StructureBlockTileEntity> {
   public StructureTileEntityRenderer(TileEntityRendererDispatcher p_i226017_1_) {
      super(p_i226017_1_);
   }

   public void render(StructureBlockTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
      if (Minecraft.getInstance().player.canUseCommandBlock() || Minecraft.getInstance().player.isSpectator()) {
         BlockPos blockpos = tileEntityIn.getPosition();
         BlockPos blockpos1 = tileEntityIn.getStructureSize();
         if (blockpos1.getX() >= 1 && blockpos1.getY() >= 1 && blockpos1.getZ() >= 1) {
            if (tileEntityIn.getMode() == StructureMode.SAVE || tileEntityIn.getMode() == StructureMode.LOAD) {
               double d0 = (double)blockpos.getX();
               double d1 = (double)blockpos.getZ();
               double d5 = (double)blockpos.getY();
               double d8 = d5 + (double)blockpos1.getY();
               double d2;
               double d3;
               switch(tileEntityIn.getMirror()) {
               case LEFT_RIGHT:
                  d2 = (double)blockpos1.getX();
                  d3 = (double)(-blockpos1.getZ());
                  break;
               case FRONT_BACK:
                  d2 = (double)(-blockpos1.getX());
                  d3 = (double)blockpos1.getZ();
                  break;
               default:
                  d2 = (double)blockpos1.getX();
                  d3 = (double)blockpos1.getZ();
               }

               double d4;
               double d6;
               double d7;
               double d9;
               switch(tileEntityIn.getRotation()) {
               case CLOCKWISE_90:
                  d4 = d3 < 0.0D ? d0 : d0 + 1.0D;
                  d6 = d2 < 0.0D ? d1 + 1.0D : d1;
                  d7 = d4 - d3;
                  d9 = d6 + d2;
                  break;
               case CLOCKWISE_180:
                  d4 = d2 < 0.0D ? d0 : d0 + 1.0D;
                  d6 = d3 < 0.0D ? d1 : d1 + 1.0D;
                  d7 = d4 - d2;
                  d9 = d6 - d3;
                  break;
               case COUNTERCLOCKWISE_90:
                  d4 = d3 < 0.0D ? d0 + 1.0D : d0;
                  d6 = d2 < 0.0D ? d1 : d1 + 1.0D;
                  d7 = d4 + d3;
                  d9 = d6 - d2;
                  break;
               default:
                  d4 = d2 < 0.0D ? d0 + 1.0D : d0;
                  d6 = d3 < 0.0D ? d1 + 1.0D : d1;
                  d7 = d4 + d2;
                  d9 = d6 + d3;
               }

               float f = 1.0F;
               float f1 = 0.9F;
               float f2 = 0.5F;
               IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getLines());
               if (tileEntityIn.getMode() == StructureMode.SAVE || tileEntityIn.showsBoundingBox()) {
                  WorldRenderer.drawBoundingBox(matrixStackIn, ivertexbuilder, d4, d5, d6, d7, d8, d9, 0.9F, 0.9F, 0.9F, 1.0F, 0.5F, 0.5F, 0.5F);
               }

               if (tileEntityIn.getMode() == StructureMode.SAVE && tileEntityIn.showsAir()) {
                  this.func_228880_a_(tileEntityIn, ivertexbuilder, blockpos, true, matrixStackIn);
                  this.func_228880_a_(tileEntityIn, ivertexbuilder, blockpos, false, matrixStackIn);
               }

            }
         }
      }
   }

   private void func_228880_a_(StructureBlockTileEntity p_228880_1_, IVertexBuilder p_228880_2_, BlockPos p_228880_3_, boolean p_228880_4_, MatrixStack p_228880_5_) {
      IBlockReader iblockreader = p_228880_1_.getWorld();
      BlockPos blockpos = p_228880_1_.getPos();
      BlockPos blockpos1 = blockpos.add(p_228880_3_);

      for(BlockPos blockpos2 : BlockPos.getAllInBoxMutable(blockpos1, blockpos1.add(p_228880_1_.getStructureSize()).add(-1, -1, -1))) {
         BlockState blockstate = iblockreader.getBlockState(blockpos2);
         boolean flag = blockstate.isAir();
         boolean flag1 = blockstate.getBlock() == Blocks.STRUCTURE_VOID;
         if (flag || flag1) {
            float f = flag ? 0.05F : 0.0F;
            double d0 = (double)((float)(blockpos2.getX() - blockpos.getX()) + 0.45F - f);
            double d1 = (double)((float)(blockpos2.getY() - blockpos.getY()) + 0.45F - f);
            double d2 = (double)((float)(blockpos2.getZ() - blockpos.getZ()) + 0.45F - f);
            double d3 = (double)((float)(blockpos2.getX() - blockpos.getX()) + 0.55F + f);
            double d4 = (double)((float)(blockpos2.getY() - blockpos.getY()) + 0.55F + f);
            double d5 = (double)((float)(blockpos2.getZ() - blockpos.getZ()) + 0.55F + f);
            if (p_228880_4_) {
               WorldRenderer.drawBoundingBox(p_228880_5_, p_228880_2_, d0, d1, d2, d3, d4, d5, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F);
            } else if (flag) {
               WorldRenderer.drawBoundingBox(p_228880_5_, p_228880_2_, d0, d1, d2, d3, d4, d5, 0.5F, 0.5F, 1.0F, 1.0F, 0.5F, 0.5F, 1.0F);
            } else {
               WorldRenderer.drawBoundingBox(p_228880_5_, p_228880_2_, d0, d1, d2, d3, d4, d5, 1.0F, 0.25F, 0.25F, 1.0F, 1.0F, 0.25F, 0.25F);
            }
         }
      }

   }

   public boolean isGlobalRenderer(StructureBlockTileEntity te) {
      return true;
   }
}