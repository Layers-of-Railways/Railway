package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;
import net.minecraft.world.gen.treedecorator.TreeDecorator;

public class HugeTreeFeatureConfig extends BaseTreeFeatureConfig {
   public final int heightInterval;
   public final int crownHeight;

   protected HugeTreeFeatureConfig(BlockStateProvider trunkProviderIn, BlockStateProvider leavesProviderIn, List<TreeDecorator> decoratorsIn, int baseHeightIn, int heightIntervalIn, int crownHeightIn) {
      super(trunkProviderIn, leavesProviderIn, decoratorsIn, baseHeightIn);
      this.heightInterval = heightIntervalIn;
      this.crownHeight = crownHeightIn;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
      Dynamic<T> dynamic = new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("height_interval"), ops.createInt(this.heightInterval), ops.createString("crown_height"), ops.createInt(this.crownHeight))));
      return dynamic.merge(super.serialize(ops));
   }

   @Override
   protected HugeTreeFeatureConfig setSapling(net.minecraftforge.common.IPlantable value) {
      super.setSapling(value);
      return this;
   }

   public static <T> HugeTreeFeatureConfig func_227277_a_(Dynamic<T> p_227277_0_) {
      BaseTreeFeatureConfig basetreefeatureconfig = BaseTreeFeatureConfig.deserialize(p_227277_0_);
      return new HugeTreeFeatureConfig(basetreefeatureconfig.trunkProvider, basetreefeatureconfig.leavesProvider, basetreefeatureconfig.decorators, basetreefeatureconfig.baseHeight, p_227277_0_.get("height_interval").asInt(0), p_227277_0_.get("crown_height").asInt(0));
   }

   public static <T> HugeTreeFeatureConfig deserializeDarkOak(Dynamic<T> data) {
      return func_227277_a_(data).setSapling((net.minecraftforge.common.IPlantable)net.minecraft.block.Blocks.DARK_OAK_SAPLING);
   }
   public static <T> HugeTreeFeatureConfig deserializeSpruce(Dynamic<T> data) {
      return func_227277_a_(data).setSapling((net.minecraftforge.common.IPlantable)net.minecraft.block.Blocks.SPRUCE_SAPLING);
   }
   public static <T> HugeTreeFeatureConfig deserializeJungle(Dynamic<T> data) {
      return func_227277_a_(data).setSapling((net.minecraftforge.common.IPlantable)net.minecraft.block.Blocks.JUNGLE_SAPLING);
   }

   public static class Builder extends BaseTreeFeatureConfig.Builder {
      private List<TreeDecorator> decorators = ImmutableList.of();
      private int baseHeight;
      private int heightInterval;
      private int crownHeight;

      public Builder(BlockStateProvider trunkProviderIn, BlockStateProvider leavesProviderIn) {
         super(trunkProviderIn, leavesProviderIn);
      }

      public HugeTreeFeatureConfig.Builder decorators(List<TreeDecorator> p_227282_1_) {
         this.decorators = p_227282_1_;
         return this;
      }

      public HugeTreeFeatureConfig.Builder baseHeight(int baseHeightIn) {
         this.baseHeight = baseHeightIn;
         return this;
      }

      public HugeTreeFeatureConfig.Builder heightInterval(int heightIntervalIn) {
         this.heightInterval = heightIntervalIn;
         return this;
      }

      public HugeTreeFeatureConfig.Builder crownHeight(int crownHeightIn) {
         this.crownHeight = crownHeightIn;
         return this;
      }

      @Override
      public HugeTreeFeatureConfig.Builder setSapling(net.minecraftforge.common.IPlantable value) {
         super.setSapling(value);
         return this;
      }

      public HugeTreeFeatureConfig build() {
         return new HugeTreeFeatureConfig(this.trunkProvider, this.leavesProvider, this.decorators, this.baseHeight, this.heightInterval, this.crownHeight).setSapling(this.sapling);
      }
   }
}