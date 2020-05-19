package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SolidFaceDebugRenderer implements DebugRenderer.IDebugRenderer {
   private final Minecraft minecraft;

   public SolidFaceDebugRenderer(Minecraft minecraftIn) {
      this.minecraft = minecraftIn;
   }

   public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, double camX, double camY, double camZ) {
      IBlockReader iblockreader = this.minecraft.player.world;
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.lineWidth(2.0F);
      RenderSystem.disableTexture();
      RenderSystem.depthMask(false);
      BlockPos blockpos = new BlockPos(camX, camY, camZ);

      for(BlockPos blockpos1 : BlockPos.getAllInBoxMutable(blockpos.add(-6, -6, -6), blockpos.add(6, 6, 6))) {
         BlockState blockstate = iblockreader.getBlockState(blockpos1);
         if (blockstate.getBlock() != Blocks.AIR) {
            VoxelShape voxelshape = blockstate.getShape(iblockreader, blockpos1);

            for(AxisAlignedBB axisalignedbb : voxelshape.toBoundingBoxList()) {
               AxisAlignedBB axisalignedbb1 = axisalignedbb.offset(blockpos1).grow(0.002D).offset(-camX, -camY, -camZ);
               double d0 = axisalignedbb1.minX;
               double d1 = axisalignedbb1.minY;
               double d2 = axisalignedbb1.minZ;
               double d3 = axisalignedbb1.maxX;
               double d4 = axisalignedbb1.maxY;
               double d5 = axisalignedbb1.maxZ;
               float f = 1.0F;
               float f1 = 0.0F;
               float f2 = 0.0F;
               float f3 = 0.5F;
               if (blockstate.isSolidSide(iblockreader, blockpos1, Direction.WEST)) {
                  Tessellator tessellator = Tessellator.getInstance();
                  BufferBuilder bufferbuilder = tessellator.getBuffer();
                  bufferbuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);
                  bufferbuilder.pos(d0, d1, d2).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder.pos(d0, d1, d5).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder.pos(d0, d4, d2).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder.pos(d0, d4, d5).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  tessellator.draw();
               }

               if (blockstate.isSolidSide(iblockreader, blockpos1, Direction.SOUTH)) {
                  Tessellator tessellator1 = Tessellator.getInstance();
                  BufferBuilder bufferbuilder1 = tessellator1.getBuffer();
                  bufferbuilder1.begin(5, DefaultVertexFormats.POSITION_COLOR);
                  bufferbuilder1.pos(d0, d4, d5).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder1.pos(d0, d1, d5).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder1.pos(d3, d4, d5).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder1.pos(d3, d1, d5).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  tessellator1.draw();
               }

               if (blockstate.isSolidSide(iblockreader, blockpos1, Direction.EAST)) {
                  Tessellator tessellator2 = Tessellator.getInstance();
                  BufferBuilder bufferbuilder2 = tessellator2.getBuffer();
                  bufferbuilder2.begin(5, DefaultVertexFormats.POSITION_COLOR);
                  bufferbuilder2.pos(d3, d1, d5).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder2.pos(d3, d1, d2).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder2.pos(d3, d4, d5).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder2.pos(d3, d4, d2).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  tessellator2.draw();
               }

               if (blockstate.isSolidSide(iblockreader, blockpos1, Direction.NORTH)) {
                  Tessellator tessellator3 = Tessellator.getInstance();
                  BufferBuilder bufferbuilder3 = tessellator3.getBuffer();
                  bufferbuilder3.begin(5, DefaultVertexFormats.POSITION_COLOR);
                  bufferbuilder3.pos(d3, d4, d2).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder3.pos(d3, d1, d2).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder3.pos(d0, d4, d2).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder3.pos(d0, d1, d2).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  tessellator3.draw();
               }

               if (blockstate.isSolidSide(iblockreader, blockpos1, Direction.DOWN)) {
                  Tessellator tessellator4 = Tessellator.getInstance();
                  BufferBuilder bufferbuilder4 = tessellator4.getBuffer();
                  bufferbuilder4.begin(5, DefaultVertexFormats.POSITION_COLOR);
                  bufferbuilder4.pos(d0, d1, d2).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder4.pos(d3, d1, d2).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder4.pos(d0, d1, d5).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder4.pos(d3, d1, d5).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  tessellator4.draw();
               }

               if (blockstate.isSolidSide(iblockreader, blockpos1, Direction.UP)) {
                  Tessellator tessellator5 = Tessellator.getInstance();
                  BufferBuilder bufferbuilder5 = tessellator5.getBuffer();
                  bufferbuilder5.begin(5, DefaultVertexFormats.POSITION_COLOR);
                  bufferbuilder5.pos(d0, d4, d2).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder5.pos(d0, d4, d5).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder5.pos(d3, d4, d2).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  bufferbuilder5.pos(d3, d4, d5).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  tessellator5.draw();
               }
            }
         }
      }

      RenderSystem.depthMask(true);
      RenderSystem.enableTexture();
      RenderSystem.disableBlend();
   }
}