package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.RandomObjectDescriptor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class PointOfInterestDebugRenderer implements DebugRenderer.IDebugRenderer {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Minecraft client;
   private final Map<BlockPos, PointOfInterestDebugRenderer.POIInfo> field_217713_c = Maps.newHashMap();
   private final Set<SectionPos> field_217714_d = Sets.newHashSet();
   private final Map<UUID, PointOfInterestDebugRenderer.BrainInfo> field_217715_e = Maps.newHashMap();
   private UUID field_217716_f;

   public PointOfInterestDebugRenderer(Minecraft client) {
      this.client = client;
   }

   public void clear() {
      this.field_217713_c.clear();
      this.field_217714_d.clear();
      this.field_217715_e.clear();
      this.field_217716_f = null;
   }

   public void func_217691_a(PointOfInterestDebugRenderer.POIInfo p_217691_1_) {
      this.field_217713_c.put(p_217691_1_.field_217755_a, p_217691_1_);
   }

   public void func_217698_a(BlockPos p_217698_1_) {
      this.field_217713_c.remove(p_217698_1_);
   }

   public void func_217706_a(BlockPos p_217706_1_, int p_217706_2_) {
      PointOfInterestDebugRenderer.POIInfo pointofinterestdebugrenderer$poiinfo = this.field_217713_c.get(p_217706_1_);
      if (pointofinterestdebugrenderer$poiinfo == null) {
         LOGGER.warn("Strange, setFreeTicketCount was called for an unknown POI: " + p_217706_1_);
      } else {
         pointofinterestdebugrenderer$poiinfo.field_217757_c = p_217706_2_;
      }
   }

   public void func_217701_a(SectionPos p_217701_1_) {
      this.field_217714_d.add(p_217701_1_);
   }

   public void func_217700_b(SectionPos p_217700_1_) {
      this.field_217714_d.remove(p_217700_1_);
   }

   public void func_217692_a(PointOfInterestDebugRenderer.BrainInfo p_217692_1_) {
      this.field_217715_e.put(p_217692_1_.field_217747_a, p_217692_1_);
   }

   public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, double camX, double camY, double camZ) {
      RenderSystem.pushMatrix();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableTexture();
      this.func_229035_a_(camX, camY, camZ);
      RenderSystem.enableTexture();
      RenderSystem.disableBlend();
      RenderSystem.popMatrix();
      if (!this.client.player.isSpectator()) {
         this.func_217710_d();
      }

   }

   private void func_229035_a_(double p_229035_1_, double p_229035_3_, double p_229035_5_) {
      BlockPos blockpos = new BlockPos(p_229035_1_, p_229035_3_, p_229035_5_);
      this.field_217714_d.forEach((p_222924_1_) -> {
         if (blockpos.withinDistance(p_222924_1_.getCenter(), 60.0D)) {
            func_217702_c(p_222924_1_);
         }

      });
      this.field_217715_e.values().forEach((p_229036_7_) -> {
         if (this.func_217694_d(p_229036_7_)) {
            this.func_229038_b_(p_229036_7_, p_229035_1_, p_229035_3_, p_229035_5_);
         }

      });

      for(BlockPos blockpos1 : this.field_217713_c.keySet()) {
         if (blockpos.withinDistance(blockpos1, 30.0D)) {
            func_217699_b(blockpos1);
         }
      }

      this.field_217713_c.values().forEach((p_222916_2_) -> {
         if (blockpos.withinDistance(p_222916_2_.field_217755_a, 30.0D)) {
            this.func_217705_b(p_222916_2_);
         }

      });
      this.func_222915_d().forEach((p_222925_2_, p_222925_3_) -> {
         if (blockpos.withinDistance(p_222925_2_, 30.0D)) {
            this.func_222921_a(p_222925_2_, p_222925_3_);
         }

      });
   }

   private static void func_217702_c(SectionPos p_217702_0_) {
      float f = 1.0F;
      BlockPos blockpos = p_217702_0_.getCenter();
      BlockPos blockpos1 = blockpos.add(-1.0D, -1.0D, -1.0D);
      BlockPos blockpos2 = blockpos.add(1.0D, 1.0D, 1.0D);
      DebugRenderer.renderBox(blockpos1, blockpos2, 0.2F, 1.0F, 0.2F, 0.15F);
   }

   private static void func_217699_b(BlockPos p_217699_0_) {
      float f = 0.05F;
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      DebugRenderer.renderBox(p_217699_0_, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
   }

   private void func_222921_a(BlockPos p_222921_1_, List<String> p_222921_2_) {
      float f = 0.05F;
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      DebugRenderer.renderBox(p_222921_1_, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
      func_222923_a("" + p_222921_2_, p_222921_1_, 0, -256);
      func_222923_a("Ghost POI", p_222921_1_, 1, -65536);
   }

   private void func_217705_b(PointOfInterestDebugRenderer.POIInfo p_217705_1_) {
      int i = 0;
      if (this.func_217696_c(p_217705_1_).size() < 4) {
         func_217695_a("" + this.func_217696_c(p_217705_1_), p_217705_1_, i, -256);
      } else {
         func_217695_a("" + this.func_217696_c(p_217705_1_).size() + " ticket holders", p_217705_1_, i, -256);
      }

      ++i;
      func_217695_a("Free tickets: " + p_217705_1_.field_217757_c, p_217705_1_, i, -256);
      ++i;
      func_217695_a(p_217705_1_.field_217756_b, p_217705_1_, i, -1);
   }

   private void func_229037_a_(PointOfInterestDebugRenderer.BrainInfo p_229037_1_, double p_229037_2_, double p_229037_4_, double p_229037_6_) {
      if (p_229037_1_.field_222930_g != null) {
         PathfindingDebugRenderer.func_229032_a_(p_229037_1_.field_222930_g, 0.5F, false, false, p_229037_2_, p_229037_4_, p_229037_6_);
      }

   }

   private void func_229038_b_(PointOfInterestDebugRenderer.BrainInfo p_229038_1_, double p_229038_2_, double p_229038_4_, double p_229038_6_) {
      boolean flag = this.func_217703_c(p_229038_1_);
      int i = 0;
      func_217693_a(p_229038_1_.field_217750_d, i, p_229038_1_.field_217749_c, -1, 0.03F);
      ++i;
      if (flag) {
         func_217693_a(p_229038_1_.field_217750_d, i, p_229038_1_.field_222928_d + " " + p_229038_1_.field_222929_e + "xp", -1, 0.02F);
         ++i;
      }

      if (flag && !p_229038_1_.field_223455_g.equals("")) {
         func_217693_a(p_229038_1_.field_217750_d, i, p_229038_1_.field_223455_g, -98404, 0.02F);
         ++i;
      }

      if (flag) {
         for(String s : p_229038_1_.field_217752_f) {
            func_217693_a(p_229038_1_.field_217750_d, i, s, -16711681, 0.02F);
            ++i;
         }
      }

      if (flag) {
         for(String s1 : p_229038_1_.field_217751_e) {
            func_217693_a(p_229038_1_.field_217750_d, i, s1, -16711936, 0.02F);
            ++i;
         }
      }

      if (p_229038_1_.field_223456_i) {
         func_217693_a(p_229038_1_.field_217750_d, i, "Wants Golem", -23296, 0.02F);
         ++i;
      }

      if (flag) {
         for(String s2 : p_229038_1_.field_223457_m) {
            if (s2.startsWith(p_229038_1_.field_217749_c)) {
               func_217693_a(p_229038_1_.field_217750_d, i, s2, -1, 0.02F);
            } else {
               func_217693_a(p_229038_1_.field_217750_d, i, s2, -23296, 0.02F);
            }

            ++i;
         }
      }

      if (flag) {
         for(String s3 : Lists.reverse(p_229038_1_.field_217753_g)) {
            func_217693_a(p_229038_1_.field_217750_d, i, s3, -3355444, 0.02F);
            ++i;
         }
      }

      if (flag) {
         this.func_229037_a_(p_229038_1_, p_229038_2_, p_229038_4_, p_229038_6_);
      }

   }

   private static void func_217695_a(String p_217695_0_, PointOfInterestDebugRenderer.POIInfo p_217695_1_, int p_217695_2_, int p_217695_3_) {
      BlockPos blockpos = p_217695_1_.field_217755_a;
      func_222923_a(p_217695_0_, blockpos, p_217695_2_, p_217695_3_);
   }

   private static void func_222923_a(String p_222923_0_, BlockPos p_222923_1_, int p_222923_2_, int p_222923_3_) {
      double d0 = 1.3D;
      double d1 = 0.2D;
      double d2 = (double)p_222923_1_.getX() + 0.5D;
      double d3 = (double)p_222923_1_.getY() + 1.3D + (double)p_222923_2_ * 0.2D;
      double d4 = (double)p_222923_1_.getZ() + 0.5D;
      DebugRenderer.renderText(p_222923_0_, d2, d3, d4, p_222923_3_, 0.02F, true, 0.0F, true);
   }

   private static void func_217693_a(IPosition p_217693_0_, int p_217693_1_, String p_217693_2_, int p_217693_3_, float p_217693_4_) {
      double d0 = 2.4D;
      double d1 = 0.25D;
      BlockPos blockpos = new BlockPos(p_217693_0_);
      double d2 = (double)blockpos.getX() + 0.5D;
      double d3 = p_217693_0_.getY() + 2.4D + (double)p_217693_1_ * 0.25D;
      double d4 = (double)blockpos.getZ() + 0.5D;
      float f = 0.5F;
      DebugRenderer.renderText(p_217693_2_, d2, d3, d4, p_217693_3_, p_217693_4_, false, 0.5F, true);
   }

   private Set<String> func_217696_c(PointOfInterestDebugRenderer.POIInfo p_217696_1_) {
      return this.func_217697_c(p_217696_1_.field_217755_a).stream().map(RandomObjectDescriptor::func_229748_a_).collect(Collectors.toSet());
   }

   private boolean func_217703_c(PointOfInterestDebugRenderer.BrainInfo p_217703_1_) {
      return Objects.equals(this.field_217716_f, p_217703_1_.field_217747_a);
   }

   private boolean func_217694_d(PointOfInterestDebugRenderer.BrainInfo p_217694_1_) {
      PlayerEntity playerentity = this.client.player;
      BlockPos blockpos = new BlockPos(playerentity.getPosX(), p_217694_1_.field_217750_d.getY(), playerentity.getPosZ());
      BlockPos blockpos1 = new BlockPos(p_217694_1_.field_217750_d);
      return blockpos.withinDistance(blockpos1, 30.0D);
   }

   private Collection<UUID> func_217697_c(BlockPos p_217697_1_) {
      return this.field_217715_e.values().stream().filter((p_217690_1_) -> {
         return p_217690_1_.func_217744_a(p_217697_1_);
      }).map(PointOfInterestDebugRenderer.BrainInfo::func_217746_a).collect(Collectors.toSet());
   }

   private Map<BlockPos, List<String>> func_222915_d() {
      Map<BlockPos, List<String>> map = Maps.newHashMap();

      for(PointOfInterestDebugRenderer.BrainInfo pointofinterestdebugrenderer$braininfo : this.field_217715_e.values()) {
         for(BlockPos blockpos : pointofinterestdebugrenderer$braininfo.field_217754_h) {
            if (!this.field_217713_c.containsKey(blockpos)) {
               List<String> list = map.get(blockpos);
               if (list == null) {
                  list = Lists.newArrayList();
                  map.put(blockpos, list);
               }

               list.add(pointofinterestdebugrenderer$braininfo.field_217749_c);
            }
         }
      }

      return map;
   }

   private void func_217710_d() {
      DebugRenderer.getTargetEntity(this.client.getRenderViewEntity(), 8).ifPresent((p_217707_1_) -> {
         this.field_217716_f = p_217707_1_.getUniqueID();
      });
   }

   @OnlyIn(Dist.CLIENT)
   public static class BrainInfo {
      public final UUID field_217747_a;
      public final int field_217748_b;
      public final String field_217749_c;
      public final String field_222928_d;
      public final int field_222929_e;
      public final IPosition field_217750_d;
      public final String field_223455_g;
      public final Path field_222930_g;
      public final boolean field_223456_i;
      public final List<String> field_217751_e = Lists.newArrayList();
      public final List<String> field_217752_f = Lists.newArrayList();
      public final List<String> field_217753_g = Lists.newArrayList();
      public final List<String> field_223457_m = Lists.newArrayList();
      public final Set<BlockPos> field_217754_h = Sets.newHashSet();

      public BrainInfo(UUID p_i51529_1_, int p_i51529_2_, String p_i51529_3_, String p_i51529_4_, int p_i51529_5_, IPosition p_i51529_6_, String p_i51529_7_, @Nullable Path p_i51529_8_, boolean p_i51529_9_) {
         this.field_217747_a = p_i51529_1_;
         this.field_217748_b = p_i51529_2_;
         this.field_217749_c = p_i51529_3_;
         this.field_222928_d = p_i51529_4_;
         this.field_222929_e = p_i51529_5_;
         this.field_217750_d = p_i51529_6_;
         this.field_223455_g = p_i51529_7_;
         this.field_222930_g = p_i51529_8_;
         this.field_223456_i = p_i51529_9_;
      }

      private boolean func_217744_a(BlockPos p_217744_1_) {
         return this.field_217754_h.stream().anyMatch(p_217744_1_::equals);
      }

      public UUID func_217746_a() {
         return this.field_217747_a;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class POIInfo {
      public final BlockPos field_217755_a;
      public String field_217756_b;
      public int field_217757_c;

      public POIInfo(BlockPos p_i50886_1_, String p_i50886_2_, int p_i50886_3_) {
         this.field_217755_a = p_i50886_1_;
         this.field_217756_b = p_i50886_2_;
         this.field_217757_c = p_i50886_3_;
      }
   }
}