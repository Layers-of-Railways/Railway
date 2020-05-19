package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EntityAIDebugRenderer implements DebugRenderer.IDebugRenderer {
   private final Minecraft client;
   private final Map<Integer, List<EntityAIDebugRenderer.Entry>> field_217685_b = Maps.newHashMap();

   public void clear() {
      this.field_217685_b.clear();
   }

   public void func_217682_a(int p_217682_1_, List<EntityAIDebugRenderer.Entry> p_217682_2_) {
      this.field_217685_b.put(p_217682_1_, p_217682_2_);
   }

   public EntityAIDebugRenderer(Minecraft client) {
      this.client = client;
   }

   public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, double camX, double camY, double camZ) {
      ActiveRenderInfo activerenderinfo = this.client.gameRenderer.getActiveRenderInfo();
      RenderSystem.pushMatrix();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableTexture();
      BlockPos blockpos = new BlockPos(activerenderinfo.getProjectedView().x, 0.0D, activerenderinfo.getProjectedView().z);
      this.field_217685_b.forEach((p_217683_1_, p_217683_2_) -> {
         for(int i = 0; i < p_217683_2_.size(); ++i) {
            EntityAIDebugRenderer.Entry entityaidebugrenderer$entry = p_217683_2_.get(i);
            if (blockpos.withinDistance(entityaidebugrenderer$entry.field_217723_a, 160.0D)) {
               double d0 = (double)entityaidebugrenderer$entry.field_217723_a.getX() + 0.5D;
               double d1 = (double)entityaidebugrenderer$entry.field_217723_a.getY() + 2.0D + (double)i * 0.25D;
               double d2 = (double)entityaidebugrenderer$entry.field_217723_a.getZ() + 0.5D;
               int j = entityaidebugrenderer$entry.field_217726_d ? -16711936 : -3355444;
               DebugRenderer.renderText(entityaidebugrenderer$entry.field_217725_c, d0, d1, d2, j);
            }
         }

      });
      RenderSystem.enableDepthTest();
      RenderSystem.enableTexture();
      RenderSystem.popMatrix();
   }

   @OnlyIn(Dist.CLIENT)
   public static class Entry {
      public final BlockPos field_217723_a;
      public final int field_217724_b;
      public final String field_217725_c;
      public final boolean field_217726_d;

      public Entry(BlockPos p_i50834_1_, int p_i50834_2_, String p_i50834_3_, boolean p_i50834_4_) {
         this.field_217723_a = p_i50834_1_;
         this.field_217724_b = p_i50834_2_;
         this.field_217725_c = p_i50834_3_;
         this.field_217726_d = p_i50834_4_;
      }
   }
}