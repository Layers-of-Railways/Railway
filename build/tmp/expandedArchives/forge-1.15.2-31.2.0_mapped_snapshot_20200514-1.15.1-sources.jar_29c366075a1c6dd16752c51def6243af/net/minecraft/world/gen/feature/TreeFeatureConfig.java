package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;
import net.minecraft.world.gen.foliageplacer.FoliagePlacer;
import net.minecraft.world.gen.foliageplacer.FoliagePlacerType;
import net.minecraft.world.gen.treedecorator.TreeDecorator;

public class TreeFeatureConfig extends BaseTreeFeatureConfig {
   public final FoliagePlacer foliagePlacer;
   public final int heightRandA;
   public final int heightRandB;
   public final int trunkHeight;
   public final int trunkHeightRandom;
   public final int trunkTopOffset;
   public final int trunkTopOffsetRandom;
   public final int foliageHeight;
   public final int foliageHeightRandom;
   public final int maxWaterDepth;
   public final boolean ignoreVines;

   protected TreeFeatureConfig(BlockStateProvider trunkProviderIn, BlockStateProvider leavesProviderIn, FoliagePlacer foliagePlacerIn, List<TreeDecorator> decoratorsIn, int baseHeightIn, int heightRandAIn, int heightRandBIn, int trunkHeightIn, int trunkHeightRandomIn, int trunkTopOffsetIn, int trunkTopOffsetRandomIn, int foliageHeightIn, int foliageHeightRandomIn, int maxWaterDepthIn, boolean ignoreVinesIn) {
      super(trunkProviderIn, leavesProviderIn, decoratorsIn, baseHeightIn);
      this.foliagePlacer = foliagePlacerIn;
      this.heightRandA = heightRandAIn;
      this.heightRandB = heightRandBIn;
      this.trunkHeight = trunkHeightIn;
      this.trunkHeightRandom = trunkHeightRandomIn;
      this.trunkTopOffset = trunkTopOffsetIn;
      this.trunkTopOffsetRandom = trunkTopOffsetRandomIn;
      this.foliageHeight = foliageHeightIn;
      this.foliageHeightRandom = foliageHeightRandomIn;
      this.maxWaterDepth = maxWaterDepthIn;
      this.ignoreVines = ignoreVinesIn;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
      ImmutableMap.Builder<T, T> builder = ImmutableMap.builder();
      builder.put(ops.createString("foliage_placer"), this.foliagePlacer.serialize(ops)).put(ops.createString("height_rand_a"), ops.createInt(this.heightRandA)).put(ops.createString("height_rand_b"), ops.createInt(this.heightRandB)).put(ops.createString("trunk_height"), ops.createInt(this.trunkHeight)).put(ops.createString("trunk_height_random"), ops.createInt(this.trunkHeightRandom)).put(ops.createString("trunk_top_offset"), ops.createInt(this.trunkTopOffset)).put(ops.createString("trunk_top_offset_random"), ops.createInt(this.trunkTopOffsetRandom)).put(ops.createString("foliage_height"), ops.createInt(this.foliageHeight)).put(ops.createString("foliage_height_random"), ops.createInt(this.foliageHeightRandom)).put(ops.createString("max_water_depth"), ops.createInt(this.maxWaterDepth)).put(ops.createString("ignore_vines"), ops.createBoolean(this.ignoreVines));
      Dynamic<T> dynamic = new Dynamic<>(ops, ops.createMap(builder.build()));
      return dynamic.merge(super.serialize(ops));
   }

   @Override
   protected TreeFeatureConfig setSapling(net.minecraftforge.common.IPlantable value) {
      super.setSapling(value);
      return this;
   }

   public static <T> TreeFeatureConfig func_227338_a_(Dynamic<T> dynamic) {
      BaseTreeFeatureConfig basetreefeatureconfig = BaseTreeFeatureConfig.deserialize(dynamic);
      FoliagePlacerType<?> foliageplacertype = Registry.FOLIAGE_PLACER_TYPE.getOrDefault(new ResourceLocation(dynamic.get("foliage_placer").get("type").asString().orElseThrow(RuntimeException::new)));
      return new TreeFeatureConfig(basetreefeatureconfig.trunkProvider, basetreefeatureconfig.leavesProvider, foliageplacertype.func_227391_a_(dynamic.get("foliage_placer").orElseEmptyMap()), basetreefeatureconfig.decorators, basetreefeatureconfig.baseHeight, dynamic.get("height_rand_a").asInt(0), dynamic.get("height_rand_b").asInt(0), dynamic.get("trunk_height").asInt(-1), dynamic.get("trunk_height_random").asInt(0), dynamic.get("trunk_top_offset").asInt(0), dynamic.get("trunk_top_offset_random").asInt(0), dynamic.get("foliage_height").asInt(-1), dynamic.get("foliage_height_random").asInt(0), dynamic.get("max_water_depth").asInt(0), dynamic.get("ignore_vines").asBoolean(false));
   }

   public static <T> TreeFeatureConfig deserializeJungle(Dynamic<T> data) {
      return func_227338_a_(data).setSapling((net.minecraftforge.common.IPlantable)net.minecraft.block.Blocks.JUNGLE_SAPLING);
   }

   public static <T> TreeFeatureConfig deserializeAcacia(Dynamic<T> data) {
      return func_227338_a_(data).setSapling((net.minecraftforge.common.IPlantable)net.minecraft.block.Blocks.ACACIA_SAPLING);
   }

   public static class Builder extends BaseTreeFeatureConfig.Builder {
      private final FoliagePlacer foliagePlacer;
      private List<TreeDecorator> decorators = ImmutableList.of();
      private int baseHeight;
      private int heightRandA;
      private int heightRandB;
      private int trunkHeight = -1;
      private int trunkHeightRandom;
      private int trunkTopOffset;
      private int trunkTopOffsetRandom;
      private int foliageHeight = -1;
      private int foliageHeightRandom;
      private int maxWaterDepth;
      private boolean ignoreVines;

      public Builder(BlockStateProvider trunkProviderIn, BlockStateProvider leavesProviderIn, FoliagePlacer foliagePlacerIn) {
         super(trunkProviderIn, leavesProviderIn);
         this.foliagePlacer = foliagePlacerIn;
      }

      public TreeFeatureConfig.Builder decorators(List<TreeDecorator> decoratorsIn) {
         this.decorators = decoratorsIn;
         return this;
      }

      public TreeFeatureConfig.Builder baseHeight(int baseHeightIn) {
         this.baseHeight = baseHeightIn;
         return this;
      }

      public TreeFeatureConfig.Builder heightRandA(int heightRandAIn) {
         this.heightRandA = heightRandAIn;
         return this;
      }

      public TreeFeatureConfig.Builder heightRandB(int heightRandBIn) {
         this.heightRandB = heightRandBIn;
         return this;
      }

      public TreeFeatureConfig.Builder trunkHeight(int trunkHeightIn) {
         this.trunkHeight = trunkHeightIn;
         return this;
      }

      public TreeFeatureConfig.Builder trunkHeightRandom(int trunkHeightRandomIn) {
         this.trunkHeightRandom = trunkHeightRandomIn;
         return this;
      }

      public TreeFeatureConfig.Builder trunkTopOffset(int trunkTopOffsetIn) {
         this.trunkTopOffset = trunkTopOffsetIn;
         return this;
      }

      public TreeFeatureConfig.Builder trunkTopOffsetRandom(int trunkTopOffsetRandomIn) {
         this.trunkTopOffsetRandom = trunkTopOffsetRandomIn;
         return this;
      }

      public TreeFeatureConfig.Builder foliageHeight(int foliageHeightIn) {
         this.foliageHeight = foliageHeightIn;
         return this;
      }

      public TreeFeatureConfig.Builder foliageHeightRandom(int foliageHeightRandomIn) {
         this.foliageHeightRandom = foliageHeightRandomIn;
         return this;
      }

      public TreeFeatureConfig.Builder maxWaterDepth(int maxWaterDepthIn) {
         this.maxWaterDepth = maxWaterDepthIn;
         return this;
      }

      public TreeFeatureConfig.Builder ignoreVines() {
         this.ignoreVines = true;
         return this;
      }

      @Override
      public TreeFeatureConfig.Builder setSapling(net.minecraftforge.common.IPlantable value) {
         super.setSapling(value);
         return this;
      }

      public TreeFeatureConfig build() {
         return new TreeFeatureConfig(this.trunkProvider, this.leavesProvider, this.foliagePlacer, this.decorators, this.baseHeight, this.heightRandA, this.heightRandB, this.trunkHeight, this.trunkHeightRandom, this.trunkTopOffset, this.trunkTopOffsetRandom, this.foliageHeight, this.foliageHeightRandom, this.maxWaterDepth, this.ignoreVines).setSapling(this.sapling);
      }
   }
}