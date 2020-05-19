package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WorldGenAttemptsDebugRenderer implements DebugRenderer.IDebugRenderer {
   private final List<BlockPos> locations = Lists.newArrayList();
   private final List<Float> sizes = Lists.newArrayList();
   private final List<Float> alphas = Lists.newArrayList();
   private final List<Float> reds = Lists.newArrayList();
   private final List<Float> greens = Lists.newArrayList();
   private final List<Float> blues = Lists.newArrayList();

   public void addAttempt(BlockPos pos, float size, float red, float green, float blue, float alpha) {
      this.locations.add(pos);
      this.sizes.add(size);
      this.alphas.add(alpha);
      this.reds.add(red);
      this.greens.add(green);
      this.blues.add(blue);
   }

   public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, double camX, double camY, double camZ) {
      RenderSystem.pushMatrix();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableTexture();
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);

      for(int i = 0; i < this.locations.size(); ++i) {
         BlockPos blockpos = this.locations.get(i);
         Float f = this.sizes.get(i);
         float f1 = f / 2.0F;
         WorldRenderer.addChainedFilledBoxVertices(bufferbuilder, (double)((float)blockpos.getX() + 0.5F - f1) - camX, (double)((float)blockpos.getY() + 0.5F - f1) - camY, (double)((float)blockpos.getZ() + 0.5F - f1) - camZ, (double)((float)blockpos.getX() + 0.5F + f1) - camX, (double)((float)blockpos.getY() + 0.5F + f1) - camY, (double)((float)blockpos.getZ() + 0.5F + f1) - camZ, this.reds.get(i), this.greens.get(i), this.blues.get(i), this.alphas.get(i));
      }

      tessellator.draw();
      RenderSystem.enableTexture();
      RenderSystem.popMatrix();
   }
}