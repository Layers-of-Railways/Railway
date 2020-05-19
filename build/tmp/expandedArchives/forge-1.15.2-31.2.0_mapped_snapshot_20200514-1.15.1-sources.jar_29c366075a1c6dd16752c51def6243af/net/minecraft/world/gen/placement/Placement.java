package net.minecraft.world.gen.placement;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public abstract class Placement<DC extends IPlacementConfig> extends net.minecraftforge.registries.ForgeRegistryEntry<Placement<?>> {
   public static final Placement<NoPlacementConfig> NOPE = register("nope", new Passthrough(NoPlacementConfig::deserialize));
   public static final Placement<FrequencyConfig> COUNT_HEIGHTMAP = register("count_heightmap", new AtSurface(FrequencyConfig::deserialize));
   public static final Placement<FrequencyConfig> COUNT_TOP_SOLID = register("count_top_solid", new TopSolid(FrequencyConfig::deserialize));
   public static final Placement<FrequencyConfig> COUNT_HEIGHTMAP_32 = register("count_heightmap_32", new SurfacePlus32(FrequencyConfig::deserialize));
   public static final Placement<FrequencyConfig> COUNT_HEIGHTMAP_DOUBLE = register("count_heightmap_double", new TwiceSurface(FrequencyConfig::deserialize));
   public static final Placement<FrequencyConfig> COUNT_HEIGHT_64 = register("count_height_64", new AtHeight64(FrequencyConfig::deserialize));
   public static final Placement<NoiseDependant> NOISE_HEIGHTMAP_32 = register("noise_heightmap_32", new SurfacePlus32WithNoise(NoiseDependant::deserialize));
   public static final Placement<NoiseDependant> NOISE_HEIGHTMAP_DOUBLE = register("noise_heightmap_double", new TwiceSurfaceWithNoise(NoiseDependant::deserialize));
   public static final Placement<ChanceConfig> CHANCE_HEIGHTMAP = register("chance_heightmap", new AtSurfaceWithChance(ChanceConfig::deserialize));
   public static final Placement<ChanceConfig> CHANCE_HEIGHTMAP_DOUBLE = register("chance_heightmap_double", new TwiceSurfaceWithChance(ChanceConfig::deserialize));
   public static final Placement<ChanceConfig> CHANCE_PASSTHROUGH = register("chance_passthrough", new WithChance(ChanceConfig::deserialize));
   public static final Placement<ChanceConfig> CHANCE_TOP_SOLID_HEIGHTMAP = register("chance_top_solid_heightmap", new TopSolidWithChance(ChanceConfig::deserialize));
   public static final Placement<AtSurfaceWithExtraConfig> COUNT_EXTRA_HEIGHTMAP = register("count_extra_heightmap", new AtSurfaceWithExtra(AtSurfaceWithExtraConfig::deserialize));
   public static final Placement<CountRangeConfig> COUNT_RANGE = register("count_range", new CountRange(CountRangeConfig::deserialize));
   public static final Placement<CountRangeConfig> COUNT_BIASED_RANGE = register("count_biased_range", new HeightBiasedRange(CountRangeConfig::deserialize));
   public static final Placement<CountRangeConfig> COUNT_VERY_BIASED_RANGE = register("count_very_biased_range", new HeightVeryBiasedRange(CountRangeConfig::deserialize));
   public static final Placement<CountRangeConfig> RANDOM_COUNT_RANGE = register("random_count_range", new RandomCountWithRange(CountRangeConfig::deserialize));
   public static final Placement<ChanceRangeConfig> CHANCE_RANGE = register("chance_range", new ChanceRange(ChanceRangeConfig::deserialize));
   public static final Placement<HeightWithChanceConfig> COUNT_CHANCE_HEIGHTMAP = register("count_chance_heightmap", new AtSurfaceWithChanceMultiple(HeightWithChanceConfig::deserialize));
   public static final Placement<HeightWithChanceConfig> COUNT_CHANCE_HEIGHTMAP_DOUBLE = register("count_chance_heightmap_double", new TwiceSurfaceWithChanceMultiple(HeightWithChanceConfig::deserialize));
   public static final Placement<DepthAverageConfig> COUNT_DEPTH_AVERAGE = register("count_depth_average", new DepthAverage(DepthAverageConfig::deserialize));
   public static final Placement<NoPlacementConfig> TOP_SOLID_HEIGHTMAP = register("top_solid_heightmap", new TopSolidOnce(NoPlacementConfig::deserialize));
   public static final Placement<TopSolidRangeConfig> TOP_SOLID_HEIGHTMAP_RANGE = register("top_solid_heightmap_range", new TopSolidRange(TopSolidRangeConfig::deserialize));
   public static final Placement<TopSolidWithNoiseConfig> TOP_SOLID_HEIGHTMAP_NOISE_BIASED = register("top_solid_heightmap_noise_biased", new TopSolidWithNoise(TopSolidWithNoiseConfig::deserialize));
   public static final Placement<CaveEdgeConfig> CARVING_MASK = register("carving_mask", new CaveEdge(CaveEdgeConfig::deserialize));
   public static final Placement<FrequencyConfig> FOREST_ROCK = register("forest_rock", new AtSurfaceRandomCount(FrequencyConfig::deserialize));
   public static final Placement<FrequencyConfig> HELL_FIRE = register("hell_fire", new NetherFire(FrequencyConfig::deserialize));
   public static final Placement<FrequencyConfig> MAGMA = register("magma", new NetherMagma(FrequencyConfig::deserialize));
   public static final Placement<NoPlacementConfig> EMERALD_ORE = register("emerald_ore", new Height4To32(NoPlacementConfig::deserialize));
   public static final Placement<ChanceConfig> LAVA_LAKE = register("lava_lake", new LakeLava(ChanceConfig::deserialize));
   public static final Placement<ChanceConfig> WATER_LAKE = register("water_lake", new LakeWater(ChanceConfig::deserialize));
   public static final Placement<ChanceConfig> DUNGEONS = register("dungeons", new DungeonRoom(ChanceConfig::deserialize));
   public static final Placement<NoPlacementConfig> DARK_OAK_TREE = register("dark_oak_tree", new DarkOakTreePlacement(NoPlacementConfig::deserialize));
   public static final Placement<ChanceConfig> ICEBERG = register("iceberg", new IcebergPlacement(ChanceConfig::deserialize));
   public static final Placement<FrequencyConfig> LIGHT_GEM_CHANCE = register("light_gem_chance", new NetherGlowstone(FrequencyConfig::deserialize));
   public static final Placement<NoPlacementConfig> END_ISLAND = register("end_island", new EndIsland(NoPlacementConfig::deserialize));
   public static final Placement<NoPlacementConfig> CHORUS_PLANT = register("chorus_plant", new ChorusPlant(NoPlacementConfig::deserialize));
   public static final Placement<NoPlacementConfig> END_GATEWAY = register("end_gateway", new EndGateway(NoPlacementConfig::deserialize));
   private final Function<Dynamic<?>, ? extends DC> configFactory;

   private static <T extends IPlacementConfig, G extends Placement<T>> G register(String key, G p_214999_1_) {
      return (G)(Registry.<Placement<?>>register(Registry.DECORATOR, key, p_214999_1_));
   }

   public Placement(Function<Dynamic<?>, ? extends DC> configFactoryIn) {
      this.configFactory = configFactoryIn;
   }

   public DC createConfig(Dynamic<?> p_215001_1_) {
      return (DC)(this.configFactory.apply(p_215001_1_));
   }

   public ConfiguredPlacement<DC> configure(DC p_227446_1_) {
      return new ConfiguredPlacement<>(this, p_227446_1_);
   }

   protected <FC extends IFeatureConfig, F extends Feature<FC>> boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> p_214998_2_, Random random, BlockPos pos, DC p_214998_5_, ConfiguredFeature<FC, F> p_214998_6_) {
      AtomicBoolean atomicboolean = new AtomicBoolean(false);
      this.getPositions(worldIn, p_214998_2_, random, p_214998_5_, pos).forEach((p_215000_5_) -> {
         boolean flag = p_214998_6_.place(worldIn, p_214998_2_, random, p_215000_5_);
         atomicboolean.set(atomicboolean.get() || flag);
      });
      return atomicboolean.get();
   }

   public abstract Stream<BlockPos> getPositions(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generatorIn, Random random, DC configIn, BlockPos pos);

   public String toString() {
      return this.getClass().getSimpleName() + "@" + Integer.toHexString(this.hashCode());
   }
}