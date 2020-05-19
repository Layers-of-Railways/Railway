package net.minecraft.client.renderer.vertex;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VertexBuffer implements AutoCloseable {
   private int glBufferId;
   private final VertexFormat vertexFormat;
   private int count;

   public VertexBuffer(VertexFormat vertexFormatIn) {
      this.vertexFormat = vertexFormatIn;
      RenderSystem.glGenBuffers((p_227876_1_) -> {
         this.glBufferId = p_227876_1_;
      });
   }

   public void bindBuffer() {
      RenderSystem.glBindBuffer(34962, () -> {
         return this.glBufferId;
      });
   }

   public void upload(BufferBuilder bufferIn) {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(() -> {
            this.uploadRaw(bufferIn);
         });
      } else {
         this.uploadRaw(bufferIn);
      }

   }

   public CompletableFuture<Void> uploadLater(BufferBuilder bufferIn) {
      if (!RenderSystem.isOnRenderThread()) {
         return CompletableFuture.runAsync(() -> {
            this.uploadRaw(bufferIn);
         }, (p_227877_0_) -> {
            RenderSystem.recordRenderCall(p_227877_0_::run);
         });
      } else {
         this.uploadRaw(bufferIn);
         return CompletableFuture.completedFuture((Void)null);
      }
   }

   private void uploadRaw(BufferBuilder bufferIn) {
      Pair<BufferBuilder.DrawState, ByteBuffer> pair = bufferIn.getNextBuffer();
      if (this.glBufferId != -1) {
         ByteBuffer bytebuffer = pair.getSecond();
         this.count = bytebuffer.remaining() / this.vertexFormat.getSize();
         this.bindBuffer();
         RenderSystem.glBufferData(34962, bytebuffer, 35044);
         unbindBuffer();
      }
   }

   public void draw(Matrix4f matrixIn, int modeIn) {
      RenderSystem.pushMatrix();
      RenderSystem.loadIdentity();
      RenderSystem.multMatrix(matrixIn);
      RenderSystem.drawArrays(modeIn, 0, this.count);
      RenderSystem.popMatrix();
   }

   public static void unbindBuffer() {
      RenderSystem.glBindBuffer(34962, () -> {
         return 0;
      });
   }

   public void close() {
      if (this.glBufferId >= 0) {
         RenderSystem.glDeleteBuffers(this.glBufferId);
         this.glBufferId = -1;
      }

   }
}