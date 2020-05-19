package com.mojang.blaze3d.systems;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallbackI;

@OnlyIn(Dist.CLIENT)
public class RenderSystem {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ConcurrentLinkedQueue<IRenderCall> recordingQueue = Queues.newConcurrentLinkedQueue();
   private static final Tessellator RENDER_THREAD_TESSELATOR = new Tessellator();
   public static final float DEFAULTALPHACUTOFF = 0.1F;
   private static final int MINIMUM_ATLAS_TEXTURE_SIZE = 1024;
   private static boolean isReplayingQueue;
   private static Thread gameThread;
   private static Thread renderThread;
   private static int MAX_SUPPORTED_TEXTURE_SIZE = -1;
   private static boolean isInInit;
   private static double lastDrawTime = Double.MIN_VALUE;

   public static void initRenderThread() {
      if (renderThread == null && gameThread != Thread.currentThread()) {
         renderThread = Thread.currentThread();
      } else {
         throw new IllegalStateException("Could not initialize render thread");
      }
   }

   public static boolean isOnRenderThread() {
      return Thread.currentThread() == renderThread;
   }

   public static boolean isOnRenderThreadOrInit() {
      return isInInit || isOnRenderThread();
   }

   public static void initGameThread(boolean p_initGameThread_0_) {
      boolean flag = renderThread == Thread.currentThread();
      if (gameThread == null && renderThread != null && flag != p_initGameThread_0_) {
         gameThread = Thread.currentThread();
      } else {
         throw new IllegalStateException("Could not initialize tick thread");
      }
   }

   public static boolean isOnGameThread() {
      return true;
   }

   public static boolean isOnGameThreadOrInit() {
      return isInInit || isOnGameThread();
   }

   public static void assertThread(Supplier<Boolean> p_assertThread_0_) {
      if (!p_assertThread_0_.get()) {
         throw new IllegalStateException("Rendersystem called from wrong thread");
      }
   }

   public static boolean isInInitPhase() {
      return true;
   }

   public static void recordRenderCall(IRenderCall p_recordRenderCall_0_) {
      recordingQueue.add(p_recordRenderCall_0_);
   }

   public static void flipFrame(long p_flipFrame_0_) {
      GLFW.glfwPollEvents();
      replayQueue();
      Tessellator.getInstance().getBuffer().reset();
      GLFW.glfwSwapBuffers(p_flipFrame_0_);
      GLFW.glfwPollEvents();
   }

   public static void replayQueue() {
      isReplayingQueue = true;

      while(!recordingQueue.isEmpty()) {
         IRenderCall irendercall = recordingQueue.poll();
         irendercall.execute();
      }

      isReplayingQueue = false;
   }

   public static void limitDisplayFPS(int p_limitDisplayFPS_0_) {
      double d0 = lastDrawTime + 1.0D / (double)p_limitDisplayFPS_0_;

      double d1;
      for(d1 = GLFW.glfwGetTime(); d1 < d0; d1 = GLFW.glfwGetTime()) {
         GLFW.glfwWaitEventsTimeout(d0 - d1);
      }

      lastDrawTime = d1;
   }

   public static void pushLightingAttributes() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.pushLightingAttributes();
   }

   public static void pushTextureAttributes() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.pushTextureAttributes();
   }

   public static void popAttributes() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.popAttributes();
   }

   public static void disableAlphaTest() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.disableAlphaTest();
   }

   public static void enableAlphaTest() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.enableAlphaTest();
   }

   public static void alphaFunc(int p_alphaFunc_0_, float p_alphaFunc_1_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.alphaFunc(p_alphaFunc_0_, p_alphaFunc_1_);
   }

   public static void enableLighting() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.enableLighting();
   }

   public static void disableLighting() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.disableLighting();
   }

   public static void enableColorMaterial() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.enableColorMaterial();
   }

   public static void disableColorMaterial() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.disableColorMaterial();
   }

   public static void colorMaterial(int p_colorMaterial_0_, int p_colorMaterial_1_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.colorMaterial(p_colorMaterial_0_, p_colorMaterial_1_);
   }

   public static void normal3f(float p_normal3f_0_, float p_normal3f_1_, float p_normal3f_2_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.normal3f(p_normal3f_0_, p_normal3f_1_, p_normal3f_2_);
   }

   public static void disableDepthTest() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.disableDepthTest();
   }

   public static void enableDepthTest() {
      assertThread(RenderSystem::isOnGameThreadOrInit);
      GlStateManager.enableDepthTest();
   }

   public static void depthFunc(int p_depthFunc_0_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.depthFunc(p_depthFunc_0_);
   }

   public static void depthMask(boolean p_depthMask_0_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.depthMask(p_depthMask_0_);
   }

   public static void enableBlend() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.enableBlend();
   }

   public static void disableBlend() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.disableBlend();
   }

   public static void blendFunc(GlStateManager.SourceFactor p_blendFunc_0_, GlStateManager.DestFactor p_blendFunc_1_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.blendFunc(p_blendFunc_0_.param, p_blendFunc_1_.param);
   }

   public static void blendFunc(int p_blendFunc_0_, int p_blendFunc_1_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.blendFunc(p_blendFunc_0_, p_blendFunc_1_);
   }

   public static void blendFuncSeparate(GlStateManager.SourceFactor p_blendFuncSeparate_0_, GlStateManager.DestFactor p_blendFuncSeparate_1_, GlStateManager.SourceFactor p_blendFuncSeparate_2_, GlStateManager.DestFactor p_blendFuncSeparate_3_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.blendFuncSeparate(p_blendFuncSeparate_0_.param, p_blendFuncSeparate_1_.param, p_blendFuncSeparate_2_.param, p_blendFuncSeparate_3_.param);
   }

   public static void blendFuncSeparate(int p_blendFuncSeparate_0_, int p_blendFuncSeparate_1_, int p_blendFuncSeparate_2_, int p_blendFuncSeparate_3_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.blendFuncSeparate(p_blendFuncSeparate_0_, p_blendFuncSeparate_1_, p_blendFuncSeparate_2_, p_blendFuncSeparate_3_);
   }

   public static void blendEquation(int p_blendEquation_0_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.blendEquation(p_blendEquation_0_);
   }

   public static void blendColor(float p_blendColor_0_, float p_blendColor_1_, float p_blendColor_2_, float p_blendColor_3_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.blendColor(p_blendColor_0_, p_blendColor_1_, p_blendColor_2_, p_blendColor_3_);
   }

   public static void enableFog() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.enableFog();
   }

   public static void disableFog() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.disableFog();
   }

   public static void fogMode(GlStateManager.FogMode p_fogMode_0_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.fogMode(p_fogMode_0_.param);
   }

   public static void fogMode(int p_fogMode_0_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.fogMode(p_fogMode_0_);
   }

   public static void fogDensity(float p_fogDensity_0_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.fogDensity(p_fogDensity_0_);
   }

   public static void fogStart(float p_fogStart_0_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.fogStart(p_fogStart_0_);
   }

   public static void fogEnd(float p_fogEnd_0_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.fogEnd(p_fogEnd_0_);
   }

   public static void fog(int p_fog_0_, float p_fog_1_, float p_fog_2_, float p_fog_3_, float p_fog_4_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.fog(p_fog_0_, new float[]{p_fog_1_, p_fog_2_, p_fog_3_, p_fog_4_});
   }

   public static void fogi(int p_fogi_0_, int p_fogi_1_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.fogi(p_fogi_0_, p_fogi_1_);
   }

   public static void enableCull() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.enableCull();
   }

   public static void disableCull() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.disableCull();
   }

   public static void polygonMode(int p_polygonMode_0_, int p_polygonMode_1_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.polygonMode(p_polygonMode_0_, p_polygonMode_1_);
   }

   public static void enablePolygonOffset() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.enablePolygonOffset();
   }

   public static void disablePolygonOffset() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.disablePolygonOffset();
   }

   public static void enableLineOffset() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.enableLineOffset();
   }

   public static void disableLineOffset() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.disableLineOffset();
   }

   public static void polygonOffset(float p_polygonOffset_0_, float p_polygonOffset_1_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.polygonOffset(p_polygonOffset_0_, p_polygonOffset_1_);
   }

   public static void enableColorLogicOp() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.enableColorLogicOp();
   }

   public static void disableColorLogicOp() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.disableColorLogicOp();
   }

   public static void logicOp(GlStateManager.LogicOp p_logicOp_0_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.logicOp(p_logicOp_0_.opcode);
   }

   public static void activeTexture(int p_activeTexture_0_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.activeTexture(p_activeTexture_0_);
   }

   public static void enableTexture() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.enableTexture();
   }

   public static void disableTexture() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.disableTexture();
   }

   public static void texParameter(int p_texParameter_0_, int p_texParameter_1_, int p_texParameter_2_) {
      GlStateManager.texParameter(p_texParameter_0_, p_texParameter_1_, p_texParameter_2_);
   }

   public static void deleteTexture(int p_deleteTexture_0_) {
      assertThread(RenderSystem::isOnGameThreadOrInit);
      GlStateManager.deleteTexture(p_deleteTexture_0_);
   }

   public static void bindTexture(int p_bindTexture_0_) {
      GlStateManager.bindTexture(p_bindTexture_0_);
   }

   public static void shadeModel(int p_shadeModel_0_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.shadeModel(p_shadeModel_0_);
   }

   public static void enableRescaleNormal() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.enableRescaleNormal();
   }

   public static void disableRescaleNormal() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.disableRescaleNormal();
   }

   public static void viewport(int p_viewport_0_, int p_viewport_1_, int p_viewport_2_, int p_viewport_3_) {
      assertThread(RenderSystem::isOnGameThreadOrInit);
      GlStateManager.viewport(p_viewport_0_, p_viewport_1_, p_viewport_2_, p_viewport_3_);
   }

   public static void colorMask(boolean p_colorMask_0_, boolean p_colorMask_1_, boolean p_colorMask_2_, boolean p_colorMask_3_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.colorMask(p_colorMask_0_, p_colorMask_1_, p_colorMask_2_, p_colorMask_3_);
   }

   public static void stencilFunc(int p_stencilFunc_0_, int p_stencilFunc_1_, int p_stencilFunc_2_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.stencilFunc(p_stencilFunc_0_, p_stencilFunc_1_, p_stencilFunc_2_);
   }

   public static void stencilMask(int p_stencilMask_0_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.stencilMask(p_stencilMask_0_);
   }

   public static void stencilOp(int p_stencilOp_0_, int p_stencilOp_1_, int p_stencilOp_2_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.stencilOp(p_stencilOp_0_, p_stencilOp_1_, p_stencilOp_2_);
   }

   public static void clearDepth(double p_clearDepth_0_) {
      assertThread(RenderSystem::isOnGameThreadOrInit);
      GlStateManager.clearDepth(p_clearDepth_0_);
   }

   public static void clearColor(float p_clearColor_0_, float p_clearColor_1_, float p_clearColor_2_, float p_clearColor_3_) {
      assertThread(RenderSystem::isOnGameThreadOrInit);
      GlStateManager.clearColor(p_clearColor_0_, p_clearColor_1_, p_clearColor_2_, p_clearColor_3_);
   }

   public static void clearStencil(int p_clearStencil_0_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.clearStencil(p_clearStencil_0_);
   }

   public static void clear(int p_clear_0_, boolean p_clear_1_) {
      assertThread(RenderSystem::isOnGameThreadOrInit);
      GlStateManager.clear(p_clear_0_, p_clear_1_);
   }

   public static void matrixMode(int p_matrixMode_0_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.matrixMode(p_matrixMode_0_);
   }

   public static void loadIdentity() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.loadIdentity();
   }

   public static void pushMatrix() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.pushMatrix();
   }

   public static void popMatrix() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.popMatrix();
   }

   public static void ortho(double p_ortho_0_, double p_ortho_2_, double p_ortho_4_, double p_ortho_6_, double p_ortho_8_, double p_ortho_10_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.ortho(p_ortho_0_, p_ortho_2_, p_ortho_4_, p_ortho_6_, p_ortho_8_, p_ortho_10_);
   }

   public static void rotatef(float p_rotatef_0_, float p_rotatef_1_, float p_rotatef_2_, float p_rotatef_3_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.rotatef(p_rotatef_0_, p_rotatef_1_, p_rotatef_2_, p_rotatef_3_);
   }

   public static void scalef(float p_scalef_0_, float p_scalef_1_, float p_scalef_2_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.scalef(p_scalef_0_, p_scalef_1_, p_scalef_2_);
   }

   public static void scaled(double p_scaled_0_, double p_scaled_2_, double p_scaled_4_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.scaled(p_scaled_0_, p_scaled_2_, p_scaled_4_);
   }

   public static void translatef(float p_translatef_0_, float p_translatef_1_, float p_translatef_2_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.translatef(p_translatef_0_, p_translatef_1_, p_translatef_2_);
   }

   public static void translated(double p_translated_0_, double p_translated_2_, double p_translated_4_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.translated(p_translated_0_, p_translated_2_, p_translated_4_);
   }

   public static void multMatrix(Matrix4f p_multMatrix_0_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.multMatrix(p_multMatrix_0_);
   }

   public static void color4f(float p_color4f_0_, float p_color4f_1_, float p_color4f_2_, float p_color4f_3_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.color4f(p_color4f_0_, p_color4f_1_, p_color4f_2_, p_color4f_3_);
   }

   public static void color3f(float p_color3f_0_, float p_color3f_1_, float p_color3f_2_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.color4f(p_color3f_0_, p_color3f_1_, p_color3f_2_, 1.0F);
   }

   public static void clearCurrentColor() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.clearCurrentColor();
   }

   public static void drawArrays(int p_drawArrays_0_, int p_drawArrays_1_, int p_drawArrays_2_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.drawArrays(p_drawArrays_0_, p_drawArrays_1_, p_drawArrays_2_);
   }

   public static void lineWidth(float p_lineWidth_0_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.lineWidth(p_lineWidth_0_);
   }

   public static void pixelStore(int p_pixelStore_0_, int p_pixelStore_1_) {
      assertThread(RenderSystem::isOnGameThreadOrInit);
      GlStateManager.pixelStore(p_pixelStore_0_, p_pixelStore_1_);
   }

   public static void pixelTransfer(int p_pixelTransfer_0_, float p_pixelTransfer_1_) {
      GlStateManager.pixelTransfer(p_pixelTransfer_0_, p_pixelTransfer_1_);
   }

   public static void readPixels(int p_readPixels_0_, int p_readPixels_1_, int p_readPixels_2_, int p_readPixels_3_, int p_readPixels_4_, int p_readPixels_5_, ByteBuffer p_readPixels_6_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.readPixels(p_readPixels_0_, p_readPixels_1_, p_readPixels_2_, p_readPixels_3_, p_readPixels_4_, p_readPixels_5_, p_readPixels_6_);
   }

   public static void getString(int p_getString_0_, Consumer<String> p_getString_1_) {
      assertThread(RenderSystem::isOnGameThread);
      p_getString_1_.accept(GlStateManager.getString(p_getString_0_));
   }

   public static String getBackendDescription() {
      assertThread(RenderSystem::isInInitPhase);
      return String.format("LWJGL version %s", GLX._getLWJGLVersion());
   }

   public static String getApiDescription() {
      assertThread(RenderSystem::isInInitPhase);
      return GLX.getOpenGLVersionString();
   }

   public static LongSupplier initBackendSystem() {
      assertThread(RenderSystem::isInInitPhase);
      return GLX._initGlfw();
   }

   public static void initRenderer(int p_initRenderer_0_, boolean p_initRenderer_1_) {
      assertThread(RenderSystem::isInInitPhase);
      GLX._init(p_initRenderer_0_, p_initRenderer_1_);
   }

   public static void setErrorCallback(GLFWErrorCallbackI p_setErrorCallback_0_) {
      assertThread(RenderSystem::isInInitPhase);
      GLX._setGlfwErrorCallback(p_setErrorCallback_0_);
   }

   public static void renderCrosshair(int p_renderCrosshair_0_) {
      assertThread(RenderSystem::isOnGameThread);
      GLX._renderCrosshair(p_renderCrosshair_0_, true, true, true);
   }

   public static void setupNvFogDistance() {
      assertThread(RenderSystem::isOnGameThread);
      GLX._setupNvFogDistance();
   }

   public static void glMultiTexCoord2f(int p_glMultiTexCoord2f_0_, float p_glMultiTexCoord2f_1_, float p_glMultiTexCoord2f_2_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.multiTexCoord2f(p_glMultiTexCoord2f_0_, p_glMultiTexCoord2f_1_, p_glMultiTexCoord2f_2_);
   }

   public static String getCapsString() {
      assertThread(RenderSystem::isOnGameThread);
      return GLX._getCapsString();
   }

   public static void setupDefaultState(int p_setupDefaultState_0_, int p_setupDefaultState_1_, int p_setupDefaultState_2_, int p_setupDefaultState_3_) {
      assertThread(RenderSystem::isInInitPhase);
      GlStateManager.enableTexture();
      GlStateManager.shadeModel(7425);
      GlStateManager.clearDepth(1.0D);
      GlStateManager.enableDepthTest();
      GlStateManager.depthFunc(515);
      GlStateManager.enableAlphaTest();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.matrixMode(5889);
      GlStateManager.loadIdentity();
      GlStateManager.matrixMode(5888);
      GlStateManager.viewport(p_setupDefaultState_0_, p_setupDefaultState_1_, p_setupDefaultState_2_, p_setupDefaultState_3_);
   }

   public static int maxSupportedTextureSize() {
      assertThread(RenderSystem::isInInitPhase);
      if (MAX_SUPPORTED_TEXTURE_SIZE == -1) {
         int i = GlStateManager.getInteger(3379);

         for(int j = Math.max(32768, i); j >= 1024; j >>= 1) {
            GlStateManager.texImage2D(32868, 0, 6408, j, j, 0, 6408, 5121, (IntBuffer)null);
            int k = GlStateManager.getTexLevelParameter(32868, 0, 4096);
            if (k != 0) {
               MAX_SUPPORTED_TEXTURE_SIZE = j;
               return j;
            }
         }

         MAX_SUPPORTED_TEXTURE_SIZE = Math.max(i, 1024);
         LOGGER.info("Failed to determine maximum texture size by probing, trying GL_MAX_TEXTURE_SIZE = {}", (int)MAX_SUPPORTED_TEXTURE_SIZE);
      }

      return MAX_SUPPORTED_TEXTURE_SIZE;
   }

   public static void glBindBuffer(int p_glBindBuffer_0_, Supplier<Integer> p_glBindBuffer_1_) {
      GlStateManager.bindBuffer(p_glBindBuffer_0_, p_glBindBuffer_1_.get());
   }

   public static void glBufferData(int p_glBufferData_0_, ByteBuffer p_glBufferData_1_, int p_glBufferData_2_) {
      assertThread(RenderSystem::isOnRenderThreadOrInit);
      GlStateManager.bufferData(p_glBufferData_0_, p_glBufferData_1_, p_glBufferData_2_);
   }

   public static void glDeleteBuffers(int p_glDeleteBuffers_0_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.deleteBuffers(p_glDeleteBuffers_0_);
   }

   public static void glUniform1i(int p_glUniform1i_0_, int p_glUniform1i_1_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.uniform1i(p_glUniform1i_0_, p_glUniform1i_1_);
   }

   public static void glUniform1(int p_glUniform1_0_, IntBuffer p_glUniform1_1_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.uniform1i(p_glUniform1_0_, p_glUniform1_1_);
   }

   public static void glUniform2(int p_glUniform2_0_, IntBuffer p_glUniform2_1_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.uniform2i(p_glUniform2_0_, p_glUniform2_1_);
   }

   public static void glUniform3(int p_glUniform3_0_, IntBuffer p_glUniform3_1_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.uniform3i(p_glUniform3_0_, p_glUniform3_1_);
   }

   public static void glUniform4(int p_glUniform4_0_, IntBuffer p_glUniform4_1_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.uniform4i(p_glUniform4_0_, p_glUniform4_1_);
   }

   public static void glUniform1(int p_glUniform1_0_, FloatBuffer p_glUniform1_1_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.uniform1f(p_glUniform1_0_, p_glUniform1_1_);
   }

   public static void glUniform2(int p_glUniform2_0_, FloatBuffer p_glUniform2_1_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.uniform2f(p_glUniform2_0_, p_glUniform2_1_);
   }

   public static void glUniform3(int p_glUniform3_0_, FloatBuffer p_glUniform3_1_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.uniform3f(p_glUniform3_0_, p_glUniform3_1_);
   }

   public static void glUniform4(int p_glUniform4_0_, FloatBuffer p_glUniform4_1_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.uniform4f(p_glUniform4_0_, p_glUniform4_1_);
   }

   public static void glUniformMatrix2(int p_glUniformMatrix2_0_, boolean p_glUniformMatrix2_1_, FloatBuffer p_glUniformMatrix2_2_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.uniformMatrix2f(p_glUniformMatrix2_0_, p_glUniformMatrix2_1_, p_glUniformMatrix2_2_);
   }

   public static void glUniformMatrix3(int p_glUniformMatrix3_0_, boolean p_glUniformMatrix3_1_, FloatBuffer p_glUniformMatrix3_2_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.uniformMatrix3f(p_glUniformMatrix3_0_, p_glUniformMatrix3_1_, p_glUniformMatrix3_2_);
   }

   public static void glUniformMatrix4(int p_glUniformMatrix4_0_, boolean p_glUniformMatrix4_1_, FloatBuffer p_glUniformMatrix4_2_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.uniformMatrix4f(p_glUniformMatrix4_0_, p_glUniformMatrix4_1_, p_glUniformMatrix4_2_);
   }

   public static void setupOutline() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.setupOutline();
   }

   public static void teardownOutline() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.teardownOutline();
   }

   public static void setupOverlayColor(IntSupplier p_setupOverlayColor_0_, int p_setupOverlayColor_1_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.setupOverlayColor(p_setupOverlayColor_0_.getAsInt(), p_setupOverlayColor_1_);
   }

   public static void teardownOverlayColor() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.teardownOverlayColor();
   }

   public static void setupLevelDiffuseLighting(Matrix4f p_setupLevelDiffuseLighting_0_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.setupWorldDiffuseLighting(p_setupLevelDiffuseLighting_0_);
   }

   public static void setupGuiFlatDiffuseLighting() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.setupGuiFlatDiffuseLighting();
   }

   public static void setupGui3DDiffuseLighting() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.setupGui3DDiffuseLighting();
   }

   public static void mulTextureByProjModelView() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.mulTextureByProjModelView();
   }

   public static void setupEndPortalTexGen() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.setupEndPortalTexGen();
   }

   public static void clearTexGen() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.clearTexGen();
   }

   public static void beginInitialization() {
      isInInit = true;
   }

   public static void finishInitialization() {
      isInInit = false;
      if (!recordingQueue.isEmpty()) {
         replayQueue();
      }

      if (!recordingQueue.isEmpty()) {
         throw new IllegalStateException("Recorded to render queue during initialization");
      }
   }

   public static void glGenBuffers(Consumer<Integer> p_glGenBuffers_0_) {
      if (!isOnRenderThread()) {
         recordRenderCall(() -> {
            p_glGenBuffers_0_.accept(GlStateManager.genBuffers());
         });
      } else {
         p_glGenBuffers_0_.accept(GlStateManager.genBuffers());
      }

   }

   public static Tessellator renderThreadTesselator() {
      assertThread(RenderSystem::isOnRenderThread);
      return RENDER_THREAD_TESSELATOR;
   }

   public static void defaultBlendFunc() {
      blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
   }

   public static void defaultAlphaFunc() {
      alphaFunc(516, 0.1F);
   }
}