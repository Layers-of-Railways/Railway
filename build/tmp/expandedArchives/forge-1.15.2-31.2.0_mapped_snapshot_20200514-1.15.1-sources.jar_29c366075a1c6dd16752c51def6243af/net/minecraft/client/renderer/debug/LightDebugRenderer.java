package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LightDebugRenderer implements DebugRenderer.IDebugRenderer {
   private final Minecraft minecraft;

   public LightDebugRenderer(Minecraft minecraftIn) {
      this.minecraft = minecraftIn;
   }

   public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, double camX, double camY, double camZ) {
      World world = this.minecraft.world;
      RenderSystem.pushMatrix();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableTexture();
      BlockPos blockpos = new BlockPos(camX, camY, camZ);
      LongSet longset = new LongOpenHashSet();

      for(BlockPos blockpos1 : BlockPos.getAllInBoxMutable(blockpos.add(-10, -10, -10), blockpos.add(10, 10, 10))) {
         int i = world.getLightFor(LightType.SKY, blockpos1);
         float f = (float)(15 - i) / 15.0F * 0.5F + 0.16F;
         int j = MathHelper.hsvToRGB(f, 0.9F, 0.9F);
         long k = SectionPos.worldToSection(blockpos1.toLong());
         if (longset.add(k)) {
            DebugRenderer.renderText(world.getChunkProvider().getLightManager().getDebugInfo(LightType.SKY, SectionPos.from(k)), (double)(SectionPos.extractX(k) * 16 + 8), (double)(SectionPos.extractY(k) * 16 + 8), (double)(SectionPos.extractZ(k) * 16 + 8), 16711680, 0.3F);
         }

         if (i != 15) {
            DebugRenderer.renderText(String.valueOf(i), (double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.25D, (double)blockpos1.getZ() + 0.5D, j);
         }
      }

      RenderSystem.enableTexture();
      RenderSystem.popMatrix();
   }
}