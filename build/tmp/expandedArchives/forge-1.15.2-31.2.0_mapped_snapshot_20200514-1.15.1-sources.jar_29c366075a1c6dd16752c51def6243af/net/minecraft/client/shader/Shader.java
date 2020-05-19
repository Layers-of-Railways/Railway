package net.minecraft.client.shader;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Shader implements AutoCloseable {
   private final ShaderInstance manager;
   public final Framebuffer framebufferIn;
   public final Framebuffer framebufferOut;
   private final List<Object> listAuxFramebuffers = Lists.newArrayList();
   private final List<String> listAuxNames = Lists.newArrayList();
   private final List<Integer> listAuxWidths = Lists.newArrayList();
   private final List<Integer> listAuxHeights = Lists.newArrayList();
   private Matrix4f projectionMatrix;

   public Shader(IResourceManager resourceManager, String programName, Framebuffer framebufferInIn, Framebuffer framebufferOutIn) throws IOException {
      this.manager = new ShaderInstance(resourceManager, programName);
      this.framebufferIn = framebufferInIn;
      this.framebufferOut = framebufferOutIn;
   }

   public void close() {
      this.manager.close();
   }

   public void addAuxFramebuffer(String auxName, Object auxFramebufferIn, int width, int height) {
      this.listAuxNames.add(this.listAuxNames.size(), auxName);
      this.listAuxFramebuffers.add(this.listAuxFramebuffers.size(), auxFramebufferIn);
      this.listAuxWidths.add(this.listAuxWidths.size(), width);
      this.listAuxHeights.add(this.listAuxHeights.size(), height);
   }

   public void setProjectionMatrix(Matrix4f p_195654_1_) {
      this.projectionMatrix = p_195654_1_;
   }

   public void render(float partialTicks) {
      this.framebufferIn.unbindFramebuffer();
      float f = (float)this.framebufferOut.framebufferTextureWidth;
      float f1 = (float)this.framebufferOut.framebufferTextureHeight;
      RenderSystem.viewport(0, 0, (int)f, (int)f1);
      this.manager.func_216537_a("DiffuseSampler", this.framebufferIn);

      for(int i = 0; i < this.listAuxFramebuffers.size(); ++i) {
         this.manager.func_216537_a(this.listAuxNames.get(i), this.listAuxFramebuffers.get(i));
         this.manager.getShaderUniform("AuxSize" + i).set((float)this.listAuxWidths.get(i), (float)this.listAuxHeights.get(i));
      }

      this.manager.getShaderUniform("ProjMat").set(this.projectionMatrix);
      this.manager.getShaderUniform("InSize").set((float)this.framebufferIn.framebufferTextureWidth, (float)this.framebufferIn.framebufferTextureHeight);
      this.manager.getShaderUniform("OutSize").set(f, f1);
      this.manager.getShaderUniform("Time").set(partialTicks);
      Minecraft minecraft = Minecraft.getInstance();
      this.manager.getShaderUniform("ScreenSize").set((float)minecraft.getMainWindow().getFramebufferWidth(), (float)minecraft.getMainWindow().getFramebufferHeight());
      this.manager.func_216535_f();
      this.framebufferOut.framebufferClear(Minecraft.IS_RUNNING_ON_MAC);
      this.framebufferOut.bindFramebuffer(false);
      RenderSystem.depthMask(false);
      BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
      bufferbuilder.pos(0.0D, 0.0D, 500.0D).color(255, 255, 255, 255).endVertex();
      bufferbuilder.pos((double)f, 0.0D, 500.0D).color(255, 255, 255, 255).endVertex();
      bufferbuilder.pos((double)f, (double)f1, 500.0D).color(255, 255, 255, 255).endVertex();
      bufferbuilder.pos(0.0D, (double)f1, 500.0D).color(255, 255, 255, 255).endVertex();
      bufferbuilder.finishDrawing();
      WorldVertexBufferUploader.draw(bufferbuilder);
      RenderSystem.depthMask(true);
      this.manager.func_216544_e();
      this.framebufferOut.unbindFramebuffer();
      this.framebufferIn.unbindFramebufferTexture();

      for(Object object : this.listAuxFramebuffers) {
         if (object instanceof Framebuffer) {
            ((Framebuffer)object).unbindFramebufferTexture();
         }
      }

   }

   public ShaderInstance getShaderManager() {
      return this.manager;
   }
}