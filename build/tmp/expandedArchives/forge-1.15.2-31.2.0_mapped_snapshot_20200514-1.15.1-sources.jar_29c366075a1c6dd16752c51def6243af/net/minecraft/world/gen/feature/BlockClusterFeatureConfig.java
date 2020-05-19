package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.blockplacer.BlockPlacer;
import net.minecraft.world.gen.blockplacer.BlockPlacerType;
import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;
import net.minecraft.world.gen.blockstateprovider.BlockStateProviderType;

public class BlockClusterFeatureConfig implements IFeatureConfig {
   public final BlockStateProvider stateProvider;
   public final BlockPlacer blockPlacer;
   public final Set<Block> whitelist;
   public final Set<BlockState> blacklist;
   public final int tryCount;
   public final int xSpread;
   public final int ySpread;
   public final int zSpread;
   public final boolean isReplaceable;
   public final boolean field_227298_k_;
   public final boolean requiresWater;

   private BlockClusterFeatureConfig(BlockStateProvider p_i225836_1_, BlockPlacer p_i225836_2_, Set<Block> p_i225836_3_, Set<BlockState> p_i225836_4_, int p_i225836_5_, int p_i225836_6_, int p_i225836_7_, int p_i225836_8_, boolean p_i225836_9_, boolean p_i225836_10_, boolean p_i225836_11_) {
      this.stateProvider = p_i225836_1_;
      this.blockPlacer = p_i225836_2_;
      this.whitelist = p_i225836_3_;
      this.blacklist = p_i225836_4_;
      this.tryCount = p_i225836_5_;
      this.xSpread = p_i225836_6_;
      this.ySpread = p_i225836_7_;
      this.zSpread = p_i225836_8_;
      this.isReplaceable = p_i225836_9_;
      this.field_227298_k_ = p_i225836_10_;
      this.requiresWater = p_i225836_11_;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
      ImmutableMap.Builder<T, T> builder = ImmutableMap.builder();
      builder.put(ops.createString("state_provider"), this.stateProvider.serialize(ops)).put(ops.createString("block_placer"), this.blockPlacer.serialize(ops)).put(ops.createString("whitelist"), ops.createList(this.whitelist.stream().map((p_227301_1_) -> {
         return BlockState.serialize(ops, p_227301_1_.getDefaultState()).getValue();
      }))).put(ops.createString("blacklist"), ops.createList(this.blacklist.stream().map((p_227302_1_) -> {
         return BlockState.serialize(ops, p_227302_1_).getValue();
      }))).put(ops.createString("tries"), ops.createInt(this.tryCount)).put(ops.createString("xspread"), ops.createInt(this.xSpread)).put(ops.createString("yspread"), ops.createInt(this.ySpread)).put(ops.createString("zspread"), ops.createInt(this.zSpread)).put(ops.createString("can_replace"), ops.createBoolean(this.isReplaceable)).put(ops.createString("project"), ops.createBoolean(this.field_227298_k_)).put(ops.createString("need_water"), ops.createBoolean(this.requiresWater));
      return new Dynamic<>(ops, ops.createMap(builder.build()));
   }

   public static <T> BlockClusterFeatureConfig deserialize(Dynamic<T> p_227300_0_) {
      BlockStateProviderType<?> blockstateprovidertype = Registry.BLOCK_STATE_PROVIDER_TYPE.getOrDefault(new ResourceLocation(p_227300_0_.get("state_provider").get("type").asString().orElseThrow(RuntimeException::new)));
      BlockPlacerType<?> blockplacertype = Registry.BLOCK_PLACER_TYPE.getOrDefault(new ResourceLocation(p_227300_0_.get("block_placer").get("type").asString().orElseThrow(RuntimeException::new)));
      return new BlockClusterFeatureConfig(blockstateprovidertype.func_227399_a_(p_227300_0_.get("state_provider").orElseEmptyMap()), blockplacertype.func_227263_a_(p_227300_0_.get("block_placer").orElseEmptyMap()), p_227300_0_.get("whitelist").asList(BlockState::deserialize).stream().map(BlockState::getBlock).collect(Collectors.toSet()), Sets.newHashSet(p_227300_0_.get("blacklist").asList(BlockState::deserialize)), p_227300_0_.get("tries").asInt(128), p_227300_0_.get("xspread").asInt(7), p_227300_0_.get("yspread").asInt(3), p_227300_0_.get("zspread").asInt(7), p_227300_0_.get("can_replace").asBoolean(false), p_227300_0_.get("project").asBoolean(true), p_227300_0_.get("need_water").asBoolean(false));
   }

   public static class Builder {
      private final BlockStateProvider stateProvider;
      private final BlockPlacer blockPlacer;
      private Set<Block> whitelist = ImmutableSet.of();
      private Set<BlockState> blacklist = ImmutableSet.of();
      private int tryCount = 64;
      private int xSpread = 7;
      private int ySpread = 3;
      private int zSpread = 7;
      private boolean isReplaceable;
      private boolean field_227312_j_ = true;
      private boolean requiresWater = false;

      public Builder(BlockStateProvider p_i225838_1_, BlockPlacer p_i225838_2_) {
         this.stateProvider = p_i225838_1_;
         this.blockPlacer = p_i225838_2_;
      }

      public BlockClusterFeatureConfig.Builder whitelist(Set<Block> p_227316_1_) {
         this.whitelist = p_227316_1_;
         return this;
      }

      public BlockClusterFeatureConfig.Builder blacklist(Set<BlockState> p_227319_1_) {
         this.blacklist = p_227319_1_;
         return this;
      }

      public BlockClusterFeatureConfig.Builder tries(int p_227315_1_) {
         this.tryCount = p_227315_1_;
         return this;
      }

      public BlockClusterFeatureConfig.Builder xSpread(int p_227318_1_) {
         this.xSpread = p_227318_1_;
         return this;
      }

      public BlockClusterFeatureConfig.Builder ySpread(int p_227321_1_) {
         this.ySpread = p_227321_1_;
         return this;
      }

      public BlockClusterFeatureConfig.Builder zSpread(int p_227323_1_) {
         this.zSpread = p_227323_1_;
         return this;
      }

      public BlockClusterFeatureConfig.Builder replaceable() {
         this.isReplaceable = true;
         return this;
      }

      public BlockClusterFeatureConfig.Builder func_227317_b_() {
         this.field_227312_j_ = false;
         return this;
      }

      public BlockClusterFeatureConfig.Builder requiresWater() {
         this.requiresWater = true;
         return this;
      }

      public BlockClusterFeatureConfig build() {
         return new BlockClusterFeatureConfig(this.stateProvider, this.blockPlacer, this.whitelist, this.blacklist, this.tryCount, this.xSpread, this.ySpread, this.zSpread, this.isReplaceable, this.field_227312_j_, this.requiresWater);
      }
   }
}