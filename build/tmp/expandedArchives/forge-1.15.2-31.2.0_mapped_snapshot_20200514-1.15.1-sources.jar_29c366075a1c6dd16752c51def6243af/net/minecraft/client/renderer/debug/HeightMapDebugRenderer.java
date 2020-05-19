package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HeightMapDebugRenderer implements DebugRenderer.IDebugRenderer {
   private final Minecraft minecraft;

   public HeightMapDebugRenderer(Minecraft minecraftIn) {
      this.minecraft = minecraftIn;
   }

   public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, double camX, double camY, double camZ) {
      IWorld iworld = this.minecraft.world;
      RenderSystem.pushMatrix();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableTexture();
      BlockPos blockpos = new BlockPos(camX, 0.0D, camZ);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);

      for(BlockPos blockpos1 : BlockPos.getAllInBoxMutable(blockpos.add(-40, 0, -40), blockpos.add(40, 0, 40))) {
         int i = iworld.getHeight(Heightmap.Type.WORLD_SURFACE_WG, blockpos1.getX(), blockpos1.getZ());
         if (iworld.getBlockState(blockpos1.add(0, i, 0).down()).isAir()) {
            WorldRenderer.addChainedFilledBoxVertices(bufferbuilder, (double)((float)blockpos1.getX() + 0.25F) - camX, (double)i - camY, (double)((float)blockpos1.getZ() + 0.25F) - camZ, (double)((float)blockpos1.getX() + 0.75F) - camX, (double)i + 0.09375D - camY, (double)((float)blockpos1.getZ() + 0.75F) - camZ, 0.0F, 0.0F, 1.0F, 0.5F);
         } else {
            WorldRenderer.addChainedFilledBoxVertices(bufferbuilder, (double)((float)blockpos1.getX() + 0.25F) - camX, (double)i - camY, (double)((float)blockpos1.getZ() + 0.25F) - camZ, (double)((float)blockpos1.getX() + 0.75F) - camX, (double)i + 0.09375D - camY, (double)((float)blockpos1.getZ() + 0.75F) - camZ, 0.0F, 1.0F, 0.0F, 0.5F);
         }
      }

      tessellator.draw();
      RenderSystem.enableTexture();
      RenderSystem.popMatrix();
   }
}