package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Collection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RaidDebugRenderer implements DebugRenderer.IDebugRenderer {
   private final Minecraft client;
   private Collection<BlockPos> field_222909_b = Lists.newArrayList();

   public RaidDebugRenderer(Minecraft client) {
      this.client = client;
   }

   public void func_222906_a(Collection<BlockPos> p_222906_1_) {
      this.field_222909_b = p_222906_1_;
   }

   public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, double camX, double camY, double camZ) {
      BlockPos blockpos = this.func_222904_c().getBlockPos();

      for(BlockPos blockpos1 : this.field_222909_b) {
         if (blockpos.withinDistance(blockpos1, 160.0D)) {
            func_222903_a(blockpos1);
         }
      }

   }

   private static void func_222903_a(BlockPos p_222903_0_) {
      DebugRenderer.renderBox(p_222903_0_.add(-0.5D, -0.5D, -0.5D), p_222903_0_.add(1.5D, 1.5D, 1.5D), 1.0F, 0.0F, 0.0F, 0.15F);
      int i = -65536;
      func_222905_a("Raid center", p_222903_0_, -65536);
   }

   private static void func_222905_a(String p_222905_0_, BlockPos p_222905_1_, int p_222905_2_) {
      double d0 = (double)p_222905_1_.getX() + 0.5D;
      double d1 = (double)p_222905_1_.getY() + 1.3D;
      double d2 = (double)p_222905_1_.getZ() + 0.5D;
      DebugRenderer.renderText(p_222905_0_, d0, d1, d2, p_222905_2_, 0.04F, true, 0.0F, true);
   }

   private ActiveRenderInfo func_222904_c() {
      return this.client.gameRenderer.getActiveRenderInfo();
   }
}