package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Map;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GameTestDebugRenderer implements DebugRenderer.IDebugRenderer {
   private final Map<BlockPos, GameTestDebugRenderer.Marker> field_229020_a_ = Maps.newHashMap();

   public void func_229022_a_(BlockPos p_229022_1_, int p_229022_2_, String p_229022_3_, int p_229022_4_) {
      this.field_229020_a_.put(p_229022_1_, new GameTestDebugRenderer.Marker(p_229022_2_, p_229022_3_, Util.milliTime() + (long)p_229022_4_));
   }

   public void clear() {
      this.field_229020_a_.clear();
   }

   public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, double camX, double camY, double camZ) {
      long i = Util.milliTime();
      this.field_229020_a_.entrySet().removeIf((p_229021_2_) -> {
         return i > (p_229021_2_.getValue()).field_229026_c_;
      });
      this.field_229020_a_.forEach(this::func_229023_a_);
   }

   private void func_229023_a_(BlockPos p_229023_1_, GameTestDebugRenderer.Marker p_229023_2_) {
      RenderSystem.pushMatrix();
      RenderSystem.enableBlend();
      RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      RenderSystem.color4f(0.0F, 1.0F, 0.0F, 0.75F);
      RenderSystem.disableTexture();
      DebugRenderer.renderBox(p_229023_1_, 0.02F, p_229023_2_.func_229027_a_(), p_229023_2_.func_229028_b_(), p_229023_2_.func_229029_c_(), p_229023_2_.func_229030_d_());
      if (!p_229023_2_.field_229025_b_.isEmpty()) {
         double d0 = (double)p_229023_1_.getX() + 0.5D;
         double d1 = (double)p_229023_1_.getY() + 1.2D;
         double d2 = (double)p_229023_1_.getZ() + 0.5D;
         DebugRenderer.renderText(p_229023_2_.field_229025_b_, d0, d1, d2, -1, 0.01F, true, 0.0F, true);
      }

      RenderSystem.enableTexture();
      RenderSystem.disableBlend();
      RenderSystem.popMatrix();
   }

   @OnlyIn(Dist.CLIENT)
   static class Marker {
      public int field_229024_a_;
      public String field_229025_b_;
      public long field_229026_c_;

      public Marker(int p_i226032_1_, String p_i226032_2_, long p_i226032_3_) {
         this.field_229024_a_ = p_i226032_1_;
         this.field_229025_b_ = p_i226032_2_;
         this.field_229026_c_ = p_i226032_3_;
      }

      public float func_229027_a_() {
         return (float)(this.field_229024_a_ >> 16 & 255) / 255.0F;
      }

      public float func_229028_b_() {
         return (float)(this.field_229024_a_ >> 8 & 255) / 255.0F;
      }

      public float func_229029_c_() {
         return (float)(this.field_229024_a_ & 255) / 255.0F;
      }

      public float func_229030_d_() {
         return (float)(this.field_229024_a_ >> 24 & 255) / 255.0F;
      }
   }
}