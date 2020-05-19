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
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.RandomObjectDescriptor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BeeDebugRenderer implements DebugRenderer.IDebugRenderer {
   private final Minecraft field_228958_a_;
   private final Map<BlockPos, BeeDebugRenderer.Hive> field_228959_b_ = Maps.newHashMap();
   private final Map<UUID, BeeDebugRenderer.Bee> field_228960_c_ = Maps.newHashMap();
   private UUID field_228961_d_;

   public BeeDebugRenderer(Minecraft p_i226027_1_) {
      this.field_228958_a_ = p_i226027_1_;
   }

   public void clear() {
      this.field_228959_b_.clear();
      this.field_228960_c_.clear();
      this.field_228961_d_ = null;
   }

   public void func_228966_a_(BeeDebugRenderer.Hive p_228966_1_) {
      this.field_228959_b_.put(p_228966_1_.field_229011_a_, p_228966_1_);
   }

   public void func_228964_a_(BeeDebugRenderer.Bee p_228964_1_) {
      this.field_228960_c_.put(p_228964_1_.field_228998_a_, p_228964_1_);
   }

   public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, double camX, double camY, double camZ) {
      RenderSystem.pushMatrix();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableTexture();
      this.func_228987_c_();
      this.func_228981_b_();
      this.func_228989_d_();
      RenderSystem.enableTexture();
      RenderSystem.disableBlend();
      RenderSystem.popMatrix();
      if (!this.field_228958_a_.player.isSpectator()) {
         this.func_228997_i_();
      }

   }

   private void func_228981_b_() {
      this.field_228960_c_.entrySet().removeIf((p_228984_1_) -> {
         return this.field_228958_a_.world.getEntityByID((p_228984_1_.getValue()).field_228999_b_) == null;
      });
   }

   private void func_228987_c_() {
      long i = this.field_228958_a_.world.getGameTime() - 20L;
      this.field_228959_b_.entrySet().removeIf((p_228962_2_) -> {
         return (p_228962_2_.getValue()).field_229016_f_ < i;
      });
   }

   private void func_228989_d_() {
      BlockPos blockpos = this.func_228995_g_().getBlockPos();
      this.field_228960_c_.values().forEach((p_228994_1_) -> {
         if (this.func_228992_e_(p_228994_1_)) {
            this.func_228988_c_(p_228994_1_);
         }

      });
      this.func_228993_f_();

      for(BlockPos blockpos1 : this.field_228959_b_.keySet()) {
         if (blockpos.withinDistance(blockpos1, 30.0D)) {
            func_228968_a_(blockpos1);
         }
      }

      Map<BlockPos, Set<UUID>> map = this.func_228991_e_();
      this.field_228959_b_.values().forEach((p_228973_3_) -> {
         if (blockpos.withinDistance(p_228973_3_.field_229011_a_, 30.0D)) {
            Set<UUID> set = map.get(p_228973_3_.field_229011_a_);
            this.func_228967_a_(p_228973_3_, (Collection<UUID>)(set == null ? Sets.newHashSet() : set));
         }

      });
      this.func_228996_h_().forEach((p_228971_2_, p_228971_3_) -> {
         if (blockpos.withinDistance(p_228971_2_, 30.0D)) {
            this.func_228972_a_(p_228971_2_, p_228971_3_);
         }

      });
   }

   private Map<BlockPos, Set<UUID>> func_228991_e_() {
      Map<BlockPos, Set<UUID>> map = Maps.newHashMap();
      this.field_228960_c_.values().forEach((p_228985_1_) -> {
         p_228985_1_.field_229006_i_.forEach((p_228986_2_) -> {
            func_228980_a_(map, p_228985_1_, p_228986_2_);
         });
      });
      return map;
   }

   private void func_228993_f_() {
      Map<BlockPos, Set<UUID>> map = Maps.newHashMap();
      this.field_228960_c_.values().stream().filter(BeeDebugRenderer.Bee::func_229010_c_).forEach((p_228979_1_) -> {
         Set<UUID> set = map.get(p_228979_1_.field_229003_f_);
         if (set == null) {
            set = Sets.newHashSet();
            map.put(p_228979_1_.field_229003_f_, set);
         }

         set.add(p_228979_1_.func_229007_a_());
      });
      map.entrySet().forEach((p_228978_0_) -> {
         BlockPos blockpos = p_228978_0_.getKey();
         Set<UUID> set = p_228978_0_.getValue();
         Set<String> set1 = set.stream().map(RandomObjectDescriptor::func_229748_a_).collect(Collectors.toSet());
         int i = 1;
         func_228976_a_(set1.toString(), blockpos, i++, -256);
         func_228976_a_("Flower", blockpos, i++, -1);
         float f = 0.05F;
         func_228969_a_(blockpos, 0.05F, 0.8F, 0.8F, 0.0F, 0.3F);
      });
   }

   private static String func_228977_a_(Collection<UUID> p_228977_0_) {
      if (p_228977_0_.isEmpty()) {
         return "-";
      } else {
         return p_228977_0_.size() > 3 ? "" + p_228977_0_.size() + " bees" : p_228977_0_.stream().map(RandomObjectDescriptor::func_229748_a_).collect(Collectors.toSet()).toString();
      }
   }

   private static void func_228980_a_(Map<BlockPos, Set<UUID>> p_228980_0_, BeeDebugRenderer.Bee p_228980_1_, BlockPos p_228980_2_) {
      Set<UUID> set = p_228980_0_.get(p_228980_2_);
      if (set == null) {
         set = Sets.newHashSet();
         p_228980_0_.put(p_228980_2_, set);
      }

      set.add(p_228980_1_.func_229007_a_());
   }

   private static void func_228968_a_(BlockPos p_228968_0_) {
      float f = 0.05F;
      func_228969_a_(p_228968_0_, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
   }

   private void func_228972_a_(BlockPos p_228972_1_, List<String> p_228972_2_) {
      float f = 0.05F;
      func_228969_a_(p_228972_1_, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
      func_228976_a_("" + p_228972_2_, p_228972_1_, 0, -256);
      func_228976_a_("Ghost Hive", p_228972_1_, 1, -65536);
   }

   private static void func_228969_a_(BlockPos p_228969_0_, float p_228969_1_, float p_228969_2_, float p_228969_3_, float p_228969_4_, float p_228969_5_) {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      DebugRenderer.renderBox(p_228969_0_, p_228969_1_, p_228969_2_, p_228969_3_, p_228969_4_, p_228969_5_);
   }

   private void func_228967_a_(BeeDebugRenderer.Hive p_228967_1_, Collection<UUID> p_228967_2_) {
      int i = 0;
      if (!p_228967_2_.isEmpty()) {
         func_228975_a_("Blacklisted by " + func_228977_a_(p_228967_2_), p_228967_1_, i++, -65536);
      }

      func_228975_a_("Out: " + func_228977_a_(this.func_228983_b_(p_228967_1_.field_229011_a_)), p_228967_1_, i++, -3355444);
      if (p_228967_1_.field_229013_c_ == 0) {
         func_228975_a_("In: -", p_228967_1_, i++, -256);
      } else if (p_228967_1_.field_229013_c_ == 1) {
         func_228975_a_("In: 1 bee", p_228967_1_, i++, -256);
      } else {
         func_228975_a_("In: " + p_228967_1_.field_229013_c_ + " bees", p_228967_1_, i++, -256);
      }

      func_228975_a_("Honey: " + p_228967_1_.field_229014_d_, p_228967_1_, i++, -23296);
      func_228975_a_(p_228967_1_.field_229012_b_ + (p_228967_1_.field_229015_e_ ? " (sedated)" : ""), p_228967_1_, i++, -1);
   }

   private void func_228982_b_(BeeDebugRenderer.Bee p_228982_1_) {
      if (p_228982_1_.field_229001_d_ != null) {
         PathfindingDebugRenderer.func_229032_a_(p_228982_1_.field_229001_d_, 0.5F, false, false, this.func_228995_g_().getProjectedView().getX(), this.func_228995_g_().getProjectedView().getY(), this.func_228995_g_().getProjectedView().getZ());
      }

   }

   private void func_228988_c_(BeeDebugRenderer.Bee p_228988_1_) {
      boolean flag = this.func_228990_d_(p_228988_1_);
      int i = 0;
      func_228974_a_(p_228988_1_.field_229000_c_, i++, p_228988_1_.toString(), -1, 0.03F);
      if (p_228988_1_.field_229002_e_ == null) {
         func_228974_a_(p_228988_1_.field_229000_c_, i++, "No hive", -98404, 0.02F);
      } else {
         func_228974_a_(p_228988_1_.field_229000_c_, i++, "Hive: " + this.func_228965_a_(p_228988_1_, p_228988_1_.field_229002_e_), -256, 0.02F);
      }

      if (p_228988_1_.field_229003_f_ == null) {
         func_228974_a_(p_228988_1_.field_229000_c_, i++, "No flower", -98404, 0.02F);
      } else {
         func_228974_a_(p_228988_1_.field_229000_c_, i++, "Flower: " + this.func_228965_a_(p_228988_1_, p_228988_1_.field_229003_f_), -256, 0.02F);
      }

      for(String s : p_228988_1_.field_229005_h_) {
         func_228974_a_(p_228988_1_.field_229000_c_, i++, s, -16711936, 0.02F);
      }

      if (flag) {
         this.func_228982_b_(p_228988_1_);
      }

      if (p_228988_1_.field_229004_g_ > 0) {
         int j = p_228988_1_.field_229004_g_ < 600 ? -3355444 : -23296;
         func_228974_a_(p_228988_1_.field_229000_c_, i++, "Travelling: " + p_228988_1_.field_229004_g_ + " ticks", j, 0.02F);
      }

   }

   private static void func_228975_a_(String p_228975_0_, BeeDebugRenderer.Hive p_228975_1_, int p_228975_2_, int p_228975_3_) {
      BlockPos blockpos = p_228975_1_.field_229011_a_;
      func_228976_a_(p_228975_0_, blockpos, p_228975_2_, p_228975_3_);
   }

   private static void func_228976_a_(String p_228976_0_, BlockPos p_228976_1_, int p_228976_2_, int p_228976_3_) {
      double d0 = 1.3D;
      double d1 = 0.2D;
      double d2 = (double)p_228976_1_.getX() + 0.5D;
      double d3 = (double)p_228976_1_.getY() + 1.3D + (double)p_228976_2_ * 0.2D;
      double d4 = (double)p_228976_1_.getZ() + 0.5D;
      DebugRenderer.renderText(p_228976_0_, d2, d3, d4, p_228976_3_, 0.02F, true, 0.0F, true);
   }

   private static void func_228974_a_(IPosition p_228974_0_, int p_228974_1_, String p_228974_2_, int p_228974_3_, float p_228974_4_) {
      double d0 = 2.4D;
      double d1 = 0.25D;
      BlockPos blockpos = new BlockPos(p_228974_0_);
      double d2 = (double)blockpos.getX() + 0.5D;
      double d3 = p_228974_0_.getY() + 2.4D + (double)p_228974_1_ * 0.25D;
      double d4 = (double)blockpos.getZ() + 0.5D;
      float f = 0.5F;
      DebugRenderer.renderText(p_228974_2_, d2, d3, d4, p_228974_3_, p_228974_4_, false, 0.5F, true);
   }

   private ActiveRenderInfo func_228995_g_() {
      return this.field_228958_a_.gameRenderer.getActiveRenderInfo();
   }

   private String func_228965_a_(BeeDebugRenderer.Bee p_228965_1_, BlockPos p_228965_2_) {
      float f = MathHelper.sqrt(p_228965_2_.distanceSq(p_228965_1_.field_229000_c_.getX(), p_228965_1_.field_229000_c_.getY(), p_228965_1_.field_229000_c_.getZ(), true));
      double d0 = (double)Math.round(f * 10.0F) / 10.0D;
      return p_228965_2_.func_229422_x_() + " (dist " + d0 + ")";
   }

   private boolean func_228990_d_(BeeDebugRenderer.Bee p_228990_1_) {
      return Objects.equals(this.field_228961_d_, p_228990_1_.field_228998_a_);
   }

   private boolean func_228992_e_(BeeDebugRenderer.Bee p_228992_1_) {
      PlayerEntity playerentity = this.field_228958_a_.player;
      BlockPos blockpos = new BlockPos(playerentity.getPosX(), p_228992_1_.field_229000_c_.getY(), playerentity.getPosZ());
      BlockPos blockpos1 = new BlockPos(p_228992_1_.field_229000_c_);
      return blockpos.withinDistance(blockpos1, 30.0D);
   }

   private Collection<UUID> func_228983_b_(BlockPos p_228983_1_) {
      return this.field_228960_c_.values().stream().filter((p_228970_1_) -> {
         return p_228970_1_.func_229008_a_(p_228983_1_);
      }).map(BeeDebugRenderer.Bee::func_229007_a_).collect(Collectors.toSet());
   }

   private Map<BlockPos, List<String>> func_228996_h_() {
      Map<BlockPos, List<String>> map = Maps.newHashMap();

      for(BeeDebugRenderer.Bee beedebugrenderer$bee : this.field_228960_c_.values()) {
         if (beedebugrenderer$bee.field_229002_e_ != null && !this.field_228959_b_.containsKey(beedebugrenderer$bee.field_229002_e_)) {
            List<String> list = map.get(beedebugrenderer$bee.field_229002_e_);
            if (list == null) {
               list = Lists.newArrayList();
               map.put(beedebugrenderer$bee.field_229002_e_, list);
            }

            list.add(beedebugrenderer$bee.func_229009_b_());
         }
      }

      return map;
   }

   private void func_228997_i_() {
      DebugRenderer.getTargetEntity(this.field_228958_a_.getRenderViewEntity(), 8).ifPresent((p_228963_1_) -> {
         this.field_228961_d_ = p_228963_1_.getUniqueID();
      });
   }

   @OnlyIn(Dist.CLIENT)
   public static class Bee {
      public final UUID field_228998_a_;
      public final int field_228999_b_;
      public final IPosition field_229000_c_;
      @Nullable
      public final Path field_229001_d_;
      @Nullable
      public final BlockPos field_229002_e_;
      @Nullable
      public final BlockPos field_229003_f_;
      public final int field_229004_g_;
      public final List<String> field_229005_h_ = Lists.newArrayList();
      public final Set<BlockPos> field_229006_i_ = Sets.newHashSet();

      public Bee(UUID p_i226028_1_, int p_i226028_2_, IPosition p_i226028_3_, Path p_i226028_4_, BlockPos p_i226028_5_, BlockPos p_i226028_6_, int p_i226028_7_) {
         this.field_228998_a_ = p_i226028_1_;
         this.field_228999_b_ = p_i226028_2_;
         this.field_229000_c_ = p_i226028_3_;
         this.field_229001_d_ = p_i226028_4_;
         this.field_229002_e_ = p_i226028_5_;
         this.field_229003_f_ = p_i226028_6_;
         this.field_229004_g_ = p_i226028_7_;
      }

      public boolean func_229008_a_(BlockPos p_229008_1_) {
         return this.field_229002_e_ != null && this.field_229002_e_.equals(p_229008_1_);
      }

      public UUID func_229007_a_() {
         return this.field_228998_a_;
      }

      public String func_229009_b_() {
         return RandomObjectDescriptor.func_229748_a_(this.field_228998_a_);
      }

      public String toString() {
         return this.func_229009_b_();
      }

      public boolean func_229010_c_() {
         return this.field_229003_f_ != null;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Hive {
      public final BlockPos field_229011_a_;
      public final String field_229012_b_;
      public final int field_229013_c_;
      public final int field_229014_d_;
      public final boolean field_229015_e_;
      public final long field_229016_f_;

      public Hive(BlockPos p_i226029_1_, String p_i226029_2_, int p_i226029_3_, int p_i226029_4_, boolean p_i226029_5_, long p_i226029_6_) {
         this.field_229011_a_ = p_i226029_1_;
         this.field_229012_b_ = p_i226029_2_;
         this.field_229013_c_ = p_i226029_3_;
         this.field_229014_d_ = p_i226029_4_;
         this.field_229015_e_ = p_i226029_5_;
         this.field_229016_f_ = p_i226029_6_;
      }
   }
}