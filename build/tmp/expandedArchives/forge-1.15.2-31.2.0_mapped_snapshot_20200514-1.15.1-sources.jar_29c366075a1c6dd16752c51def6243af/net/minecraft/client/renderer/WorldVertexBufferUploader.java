package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.system.MemoryUtil;

@OnlyIn(Dist.CLIENT)
public class WorldVertexBufferUploader {
   public static void draw(BufferBuilder bufferBuilderIn) {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(() -> {
            Pair<BufferBuilder.DrawState, ByteBuffer> pair1 = bufferBuilderIn.getNextBuffer();
            BufferBuilder.DrawState bufferbuilder$drawstate1 = pair1.getFirst();
            draw(pair1.getSecond(), bufferbuilder$drawstate1.getDrawMode(), bufferbuilder$drawstate1.getFormat(), bufferbuilder$drawstate1.getVertexCount());
         });
      } else {
         Pair<BufferBuilder.DrawState, ByteBuffer> pair = bufferBuilderIn.getNextBuffer();
         BufferBuilder.DrawState bufferbuilder$drawstate = pair.getFirst();
         draw(pair.getSecond(), bufferbuilder$drawstate.getDrawMode(), bufferbuilder$drawstate.getFormat(), bufferbuilder$drawstate.getVertexCount());
      }

   }

   private static void draw(ByteBuffer bufferIn, int modeIn, VertexFormat vertexFormatIn, int countIn) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      ((Buffer)bufferIn).clear();
      if (countIn > 0) {
         vertexFormatIn.setupBufferState(MemoryUtil.memAddress(bufferIn));
         GlStateManager.drawArrays(modeIn, 0, countIn);
         vertexFormatIn.clearBufferState();
      }
   }
}