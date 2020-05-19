package net.minecraft.world.gen.feature;

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
import net.minecraft.world.gen.placement.ConfiguredPlacement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfiguredFeature<FC extends IFeatureConfig, F extends Feature<FC>> {
   public static final Logger LOGGER = LogManager.getLogger();
   public final F feature;
   public final FC config;

   public ConfiguredFeature(F featureIn, FC configIn) {
      this.feature = featureIn;
      this.config = configIn;
   }

   public ConfiguredFeature(F featureIn, Dynamic<?> dynamicIn) {
      this(featureIn, (FC)featureIn.createConfig(dynamicIn));
   }

   public ConfiguredFeature<?, ?> withPlacement(ConfiguredPlacement<?> p_227228_1_) {
      Feature<DecoratedFeatureConfig> feature = this.feature instanceof FlowersFeature ? Feature.DECORATED_FLOWER : Feature.DECORATED;
      return feature.withConfiguration(new DecoratedFeatureConfig(this, p_227228_1_));
   }

   public ConfiguredRandomFeatureList<FC> withChance(float p_227227_1_) {
      return new ConfiguredRandomFeatureList<>(this, p_227227_1_);
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> opsIn) {
      return new Dynamic<>(opsIn, opsIn.createMap(ImmutableMap.of(opsIn.createString("name"), opsIn.createString(Registry.FEATURE.getKey(this.feature).toString()), opsIn.createString("config"), this.config.serialize(opsIn).getValue())));
   }

   public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos) {
      return this.feature.place(worldIn, generator, rand, pos, this.config);
   }

   public static <T> ConfiguredFeature<?, ?> deserialize(Dynamic<T> p_222736_0_) {
      String s = p_222736_0_.get("name").asString("");
      Feature<? extends IFeatureConfig> feature = Registry.FEATURE.getOrDefault(new ResourceLocation(s));

      try {
         return new ConfiguredFeature<>(feature, p_222736_0_.get("config").orElseEmptyMap());
      } catch (RuntimeException var4) {
         LOGGER.warn("Error while deserializing {}", (Object)s);
         return new ConfiguredFeature<>(Feature.NO_OP, NoFeatureConfig.NO_FEATURE_CONFIG);
      }
   }
}