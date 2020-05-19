package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IRenderTypeBuffer {
   static IRenderTypeBuffer.Impl getImpl(BufferBuilder builderIn) {
      return getImpl(ImmutableMap.of(), builderIn);
   }

   static IRenderTypeBuffer.Impl getImpl(Map<RenderType, BufferBuilder> mapBuildersIn, BufferBuilder builderIn) {
      return new IRenderTypeBuffer.Impl(builderIn, mapBuildersIn);
   }

   IVertexBuilder getBuffer(RenderType p_getBuffer_1_);

   @OnlyIn(Dist.CLIENT)
   public static class Impl implements IRenderTypeBuffer {
      protected final BufferBuilder buffer;
      protected final Map<RenderType, BufferBuilder> fixedBuffers;
      protected Optional<RenderType> lastRenderType = Optional.empty();
      protected final Set<BufferBuilder> startedBuffers = Sets.newHashSet();

      protected Impl(BufferBuilder bufferIn, Map<RenderType, BufferBuilder> fixedBuffersIn) {
         this.buffer = bufferIn;
         this.fixedBuffers = fixedBuffersIn;
      }

      public IVertexBuilder getBuffer(RenderType p_getBuffer_1_) {
         Optional<RenderType> optional = p_getBuffer_1_.func_230169_u_();
         BufferBuilder bufferbuilder = this.getBufferRaw(p_getBuffer_1_);
         if (!Objects.equals(this.lastRenderType, optional)) {
            if (this.lastRenderType.isPresent()) {
               RenderType rendertype = this.lastRenderType.get();
               if (!this.fixedBuffers.containsKey(rendertype)) {
                  this.finish(rendertype);
               }
            }

            if (this.startedBuffers.add(bufferbuilder)) {
               bufferbuilder.begin(p_getBuffer_1_.getDrawMode(), p_getBuffer_1_.getVertexFormat());
            }

            this.lastRenderType = optional;
         }

         return bufferbuilder;
      }

      private BufferBuilder getBufferRaw(RenderType renderTypeIn) {
         return this.fixedBuffers.getOrDefault(renderTypeIn, this.buffer);
      }

      public void finish() {
         this.lastRenderType.ifPresent((p_228464_1_) -> {
            IVertexBuilder ivertexbuilder = this.getBuffer(p_228464_1_);
            if (ivertexbuilder == this.buffer) {
               this.finish(p_228464_1_);
            }

         });

         for(RenderType rendertype : this.fixedBuffers.keySet()) {
            this.finish(rendertype);
         }

      }

      public void finish(RenderType renderTypeIn) {
         BufferBuilder bufferbuilder = this.getBufferRaw(renderTypeIn);
         boolean flag = Objects.equals(this.lastRenderType, renderTypeIn.func_230169_u_());
         if (flag || bufferbuilder != this.buffer) {
            if (this.startedBuffers.remove(bufferbuilder)) {
               renderTypeIn.finish(bufferbuilder, 0, 0, 0);
               if (flag) {
                  this.lastRenderType = Optional.empty();
               }

            }
         }
      }
   }
}