package net.minecraft.world.gen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class ConfiguredPlacement<DC extends IPlacementConfig> {
   public final Placement<DC> decorator;
   public final DC config;

   public ConfiguredPlacement(Placement<DC> decorator, Dynamic<?> config) {
      this(decorator, decorator.createConfig(config));
   }

   public ConfiguredPlacement(Placement<DC> decorator, DC config) {
      this.decorator = decorator;
      this.config = config;
   }

   public <FC extends IFeatureConfig, F extends Feature<FC>> boolean place(IWorld p_215093_1_, ChunkGenerator<? extends GenerationSettings> p_215093_2_, Random p_215093_3_, BlockPos p_215093_4_, ConfiguredFeature<FC, F> p_215093_5_) {
      return this.decorator.place(p_215093_1_, p_215093_2_, p_215093_3_, p_215093_4_, this.config, p_215093_5_);
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_215094_1_) {
      return new Dynamic<>(p_215094_1_, p_215094_1_.createMap(ImmutableMap.of(p_215094_1_.createString("name"), p_215094_1_.createString(Registry.DECORATOR.getKey(this.decorator).toString()), p_215094_1_.createString("config"), this.config.serialize(p_215094_1_).getValue())));
   }

   public static <T> ConfiguredPlacement<?> deserialize(Dynamic<T> p_215095_0_) {
      Placement<? extends IPlacementConfig> placement = Registry.DECORATOR.getOrDefault(new ResourceLocation(p_215095_0_.get("name").asString("")));
      return new ConfiguredPlacement<>(placement, p_215095_0_.get("config").orElseEmptyMap());
   }
}