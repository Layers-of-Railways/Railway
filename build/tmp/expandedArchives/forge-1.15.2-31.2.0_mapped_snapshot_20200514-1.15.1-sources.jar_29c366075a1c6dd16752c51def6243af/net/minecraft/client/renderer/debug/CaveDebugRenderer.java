package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CaveDebugRenderer implements DebugRenderer.IDebugRenderer {
   private final Map<BlockPos, BlockPos> subCaves = Maps.newHashMap();
   private final Map<BlockPos, Float> sizes = Maps.newHashMap();
   private final List<BlockPos> caves = Lists.newArrayList();

   public void addCave(BlockPos cavePos, List<BlockPos> subPositions, List<Float> sizes) {
      for(int i = 0; i < subPositions.size(); ++i) {
         this.subCaves.put(subPositions.get(i), cavePos);
         this.sizes.put(subPositions.get(i), sizes.get(i));
      }

      this.caves.add(cavePos);
   }

   public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, double camX, double camY, double camZ) {
      RenderSystem.pushMatrix();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableTexture();
      BlockPos blockpos = new BlockPos(camX, 0.0D, camZ);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);

      for(Entry<BlockPos, BlockPos> entry : this.subCaves.entrySet()) {
         BlockPos blockpos1 = entry.getKey();
         BlockPos blockpos2 = entry.getValue();
         float f = (float)(blockpos2.getX() * 128 % 256) / 256.0F;
         float f1 = (float)(blockpos2.getY() * 128 % 256) / 256.0F;
         float f2 = (float)(blockpos2.getZ() * 128 % 256) / 256.0F;
         float f3 = this.sizes.get(blockpos1);
         if (blockpos.withinDistance(blockpos1, 160.0D)) {
            WorldRenderer.addChainedFilledBoxVertices(bufferbuilder, (double)((float)blockpos1.getX() + 0.5F) - camX - (double)f3, (double)((float)blockpos1.getY() + 0.5F) - camY - (double)f3, (double)((float)blockpos1.getZ() + 0.5F) - camZ - (double)f3, (double)((float)blockpos1.getX() + 0.5F) - camX + (double)f3, (double)((float)blockpos1.getY() + 0.5F) - camY + (double)f3, (double)((float)blockpos1.getZ() + 0.5F) - camZ + (double)f3, f, f1, f2, 0.5F);
         }
      }

      for(BlockPos blockpos3 : this.caves) {
         if (blockpos.withinDistance(blockpos3, 160.0D)) {
            WorldRenderer.addChainedFilledBoxVertices(bufferbuilder, (double)blockpos3.getX() - camX, (double)blockpos3.getY() - camY, (double)blockpos3.getZ() - camZ, (double)((float)blockpos3.getX() + 1.0F) - camX, (double)((float)blockpos3.getY() + 1.0F) - camY, (double)((float)blockpos3.getZ() + 1.0F) - camZ, 1.0F, 1.0F, 1.0F, 1.0F);
         }
      }

      tessellator.draw();
      RenderSystem.enableDepthTest();
      RenderSystem.enableTexture();
      RenderSystem.popMatrix();
   }
}