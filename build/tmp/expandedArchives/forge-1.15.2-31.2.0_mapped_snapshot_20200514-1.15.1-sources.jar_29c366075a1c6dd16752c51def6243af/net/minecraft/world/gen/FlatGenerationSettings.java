package net.minecraft.world.gen;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.feature.BlockStateFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.structure.MineshaftConfig;
import net.minecraft.world.gen.feature.structure.MineshaftStructure;
import net.minecraft.world.gen.feature.structure.OceanRuinConfig;
import net.minecraft.world.gen.feature.structure.OceanRuinStructure;
import net.minecraft.world.gen.feature.structure.ShipwreckConfig;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FlatGenerationSettings extends GenerationSettings {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ConfiguredFeature<?, ?> MINESHAFT = Feature.MINESHAFT.withConfiguration(new MineshaftConfig(0.004D, MineshaftStructure.Type.NORMAL)).withPlacement(Placement.NOPE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG));
   private static final ConfiguredFeature<?, ?> VILLAGE = Feature.VILLAGE.withConfiguration(new VillageConfig("village/plains/town_centers", 6)).withPlacement(Placement.NOPE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG));
   private static final ConfiguredFeature<?, ?> STRONGHOLD = Feature.STRONGHOLD.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.NOPE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG));
   private static final ConfiguredFeature<?, ?> SWAMP_HUT = Feature.SWAMP_HUT.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.NOPE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG));
   private static final ConfiguredFeature<?, ?> DESERT_PYRAMID = Feature.DESERT_PYRAMID.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.NOPE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG));
   private static final ConfiguredFeature<?, ?> JUNGLE_TEMPLE = Feature.JUNGLE_TEMPLE.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.NOPE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG));
   private static final ConfiguredFeature<?, ?> IGLOO = Feature.IGLOO.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.NOPE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG));
   private static final ConfiguredFeature<?, ?> SHIPWRECK = Feature.SHIPWRECK.withConfiguration(new ShipwreckConfig(false)).withPlacement(Placement.NOPE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG));
   private static final ConfiguredFeature<?, ?> OCEAN_MONUMENT = Feature.OCEAN_MONUMENT.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.NOPE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG));
   private static final ConfiguredFeature<?, ?> LAKE_WATER = Feature.LAKE.withConfiguration(new BlockStateFeatureConfig(Blocks.WATER.getDefaultState())).withPlacement(Placement.WATER_LAKE.configure(new ChanceConfig(4)));
   private static final ConfiguredFeature<?, ?> LAKE_LAVA = Feature.LAKE.withConfiguration(new BlockStateFeatureConfig(Blocks.LAVA.getDefaultState())).withPlacement(Placement.LAVA_LAKE.configure(new ChanceConfig(80)));
   private static final ConfiguredFeature<?, ?> END_CITY = Feature.END_CITY.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.NOPE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG));
   private static final ConfiguredFeature<?, ?> WOODLAND_MANSION = Feature.WOODLAND_MANSION.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.NOPE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG));
   private static final ConfiguredFeature<?, ?> FORTRESS = Feature.NETHER_BRIDGE.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.NOPE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG));
   private static final ConfiguredFeature<?, ?> OCEAN_RUIN = Feature.OCEAN_RUIN.withConfiguration(new OceanRuinConfig(OceanRuinStructure.Type.COLD, 0.3F, 0.1F)).withPlacement(Placement.NOPE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG));
   private static final ConfiguredFeature<?, ?> PILLAGER_OUTPOST = Feature.PILLAGER_OUTPOST.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.NOPE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG));
   public static final Map<ConfiguredFeature<?, ?>, GenerationStage.Decoration> FEATURE_STAGES = Util.make(Maps.newHashMap(), (p_209406_0_) -> {
      p_209406_0_.put(MINESHAFT, GenerationStage.Decoration.UNDERGROUND_STRUCTURES);
      p_209406_0_.put(VILLAGE, GenerationStage.Decoration.SURFACE_STRUCTURES);
      p_209406_0_.put(STRONGHOLD, GenerationStage.Decoration.UNDERGROUND_STRUCTURES);
      p_209406_0_.put(SWAMP_HUT, GenerationStage.Decoration.SURFACE_STRUCTURES);
      p_209406_0_.put(DESERT_PYRAMID, GenerationStage.Decoration.SURFACE_STRUCTURES);
      p_209406_0_.put(JUNGLE_TEMPLE, GenerationStage.Decoration.SURFACE_STRUCTURES);
      p_209406_0_.put(IGLOO, GenerationStage.Decoration.SURFACE_STRUCTURES);
      p_209406_0_.put(SHIPWRECK, GenerationStage.Decoration.SURFACE_STRUCTURES);
      p_209406_0_.put(OCEAN_RUIN, GenerationStage.Decoration.SURFACE_STRUCTURES);
      p_209406_0_.put(LAKE_WATER, GenerationStage.Decoration.LOCAL_MODIFICATIONS);
      p_209406_0_.put(LAKE_LAVA, GenerationStage.Decoration.LOCAL_MODIFICATIONS);
      p_209406_0_.put(END_CITY, GenerationStage.Decoration.SURFACE_STRUCTURES);
      p_209406_0_.put(WOODLAND_MANSION, GenerationStage.Decoration.SURFACE_STRUCTURES);
      p_209406_0_.put(FORTRESS, GenerationStage.Decoration.UNDERGROUND_STRUCTURES);
      p_209406_0_.put(OCEAN_MONUMENT, GenerationStage.Decoration.SURFACE_STRUCTURES);
      p_209406_0_.put(PILLAGER_OUTPOST, GenerationStage.Decoration.SURFACE_STRUCTURES);
   });
   public static final Map<String, ConfiguredFeature<?, ?>[]> STRUCTURES = Util.make(Maps.newHashMap(), (p_209404_0_) -> {
      p_209404_0_.put("mineshaft", new ConfiguredFeature[]{MINESHAFT});
      p_209404_0_.put("village", new ConfiguredFeature[]{VILLAGE});
      p_209404_0_.put("stronghold", new ConfiguredFeature[]{STRONGHOLD});
      p_209404_0_.put("biome_1", new ConfiguredFeature[]{SWAMP_HUT, DESERT_PYRAMID, JUNGLE_TEMPLE, IGLOO, OCEAN_RUIN, SHIPWRECK});
      p_209404_0_.put("oceanmonument", new ConfiguredFeature[]{OCEAN_MONUMENT});
      p_209404_0_.put("lake", new ConfiguredFeature[]{LAKE_WATER});
      p_209404_0_.put("lava_lake", new ConfiguredFeature[]{LAKE_LAVA});
      p_209404_0_.put("endcity", new ConfiguredFeature[]{END_CITY});
      p_209404_0_.put("mansion", new ConfiguredFeature[]{WOODLAND_MANSION});
      p_209404_0_.put("fortress", new ConfiguredFeature[]{FORTRESS});
      p_209404_0_.put("pillager_outpost", new ConfiguredFeature[]{PILLAGER_OUTPOST});
   });
   public static final Map<ConfiguredFeature<?, ?>, IFeatureConfig> FEATURE_CONFIGS = Util.make(Maps.newHashMap(), (p_209405_0_) -> {
      p_209405_0_.put(MINESHAFT, new MineshaftConfig(0.004D, MineshaftStructure.Type.NORMAL));
      p_209405_0_.put(VILLAGE, new VillageConfig("village/plains/town_centers", 6));
      p_209405_0_.put(STRONGHOLD, IFeatureConfig.NO_FEATURE_CONFIG);
      p_209405_0_.put(SWAMP_HUT, IFeatureConfig.NO_FEATURE_CONFIG);
      p_209405_0_.put(DESERT_PYRAMID, IFeatureConfig.NO_FEATURE_CONFIG);
      p_209405_0_.put(JUNGLE_TEMPLE, IFeatureConfig.NO_FEATURE_CONFIG);
      p_209405_0_.put(IGLOO, IFeatureConfig.NO_FEATURE_CONFIG);
      p_209405_0_.put(OCEAN_RUIN, new OceanRuinConfig(OceanRuinStructure.Type.COLD, 0.3F, 0.9F));
      p_209405_0_.put(SHIPWRECK, new ShipwreckConfig(false));
      p_209405_0_.put(OCEAN_MONUMENT, IFeatureConfig.NO_FEATURE_CONFIG);
      p_209405_0_.put(END_CITY, IFeatureConfig.NO_FEATURE_CONFIG);
      p_209405_0_.put(WOODLAND_MANSION, IFeatureConfig.NO_FEATURE_CONFIG);
      p_209405_0_.put(FORTRESS, IFeatureConfig.NO_FEATURE_CONFIG);
      p_209405_0_.put(PILLAGER_OUTPOST, IFeatureConfig.NO_FEATURE_CONFIG);
   });
   private final List<FlatLayerInfo> flatLayers = Lists.newArrayList();
   private final Map<String, Map<String, String>> worldFeatures = Maps.newHashMap();
   private Biome biomeToUse;
   private final BlockState[] states = new BlockState[256];
   private boolean allAir;
   private int field_202246_E;

   @Nullable
   public static Block getBlock(String p_212683_0_) {
      try {
         ResourceLocation resourcelocation = new ResourceLocation(p_212683_0_);
         return Registry.BLOCK.getValue(resourcelocation).orElse((Block)null);
      } catch (IllegalArgumentException illegalargumentexception) {
         LOGGER.warn("Invalid blockstate: {}", p_212683_0_, illegalargumentexception);
         return null;
      }
   }

   /**
    * Return the biome used on this preset.
    */
   public Biome getBiome() {
      return this.biomeToUse;
   }

   /**
    * Set the biome used on this preset.
    */
   public void setBiome(Biome biome) {
      this.biomeToUse = biome;
   }

   /**
    * Return the list of world features enabled on this preset.
    */
   public Map<String, Map<String, String>> getWorldFeatures() {
      return this.worldFeatures;
   }

   /**
    * Return the list of layers on this preset.
    */
   public List<FlatLayerInfo> getFlatLayers() {
      return this.flatLayers;
   }

   public void updateLayers() {
      int i = 0;

      for(FlatLayerInfo flatlayerinfo : this.flatLayers) {
         flatlayerinfo.setMinY(i);
         i += flatlayerinfo.getLayerCount();
      }

      this.field_202246_E = 0;
      this.allAir = true;
      i = 0;

      for(FlatLayerInfo flatlayerinfo1 : this.flatLayers) {
         for(int j = flatlayerinfo1.getMinY(); j < flatlayerinfo1.getMinY() + flatlayerinfo1.getLayerCount(); ++j) {
            BlockState blockstate = flatlayerinfo1.getLayerMaterial();
            if (blockstate.getBlock() != Blocks.AIR) {
               this.allAir = false;
               this.states[j] = blockstate;
            }
         }

         if (flatlayerinfo1.getLayerMaterial().getBlock() == Blocks.AIR) {
            i += flatlayerinfo1.getLayerCount();
         } else {
            this.field_202246_E += flatlayerinfo1.getLayerCount() + i;
            i = 0;
         }
      }

   }

   public String toString() {
      StringBuilder stringbuilder = new StringBuilder();

      for(int i = 0; i < this.flatLayers.size(); ++i) {
         if (i > 0) {
            stringbuilder.append(",");
         }

         stringbuilder.append(this.flatLayers.get(i));
      }

      stringbuilder.append(";");
      stringbuilder.append((Object)Registry.BIOME.getKey(this.biomeToUse));
      stringbuilder.append(";");
      if (!this.worldFeatures.isEmpty()) {
         int k = 0;

         for(Entry<String, Map<String, String>> entry : this.worldFeatures.entrySet()) {
            if (k++ > 0) {
               stringbuilder.append(",");
            }

            stringbuilder.append(entry.getKey().toLowerCase(Locale.ROOT));
            Map<String, String> map = entry.getValue();
            if (!map.isEmpty()) {
               stringbuilder.append("(");
               int j = 0;

               for(Entry<String, String> entry1 : map.entrySet()) {
                  if (j++ > 0) {
                     stringbuilder.append(" ");
                  }

                  stringbuilder.append(entry1.getKey());
                  stringbuilder.append("=");
                  stringbuilder.append(entry1.getValue());
               }

               stringbuilder.append(")");
            }
         }
      }

      return stringbuilder.toString();
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   private static FlatLayerInfo deserializeLayer(String p_197526_0_, int p_197526_1_) {
      String[] astring = p_197526_0_.split("\\*", 2);
      int i;
      if (astring.length == 2) {
         try {
            i = Math.max(Integer.parseInt(astring[0]), 0);
         } catch (NumberFormatException numberformatexception) {
            LOGGER.error("Error while parsing flat world string => {}", (Object)numberformatexception.getMessage());
            return null;
         }
      } else {
         i = 1;
      }

      int j = Math.min(p_197526_1_ + i, 256);
      int k = j - p_197526_1_;

      Block block;
      try {
         block = getBlock(astring[astring.length - 1]);
      } catch (Exception exception) {
         LOGGER.error("Error while parsing flat world string => {}", (Object)exception.getMessage());
         return null;
      }

      if (block == null) {
         LOGGER.error("Error while parsing flat world string => Unknown block, {}", (Object)astring[astring.length - 1]);
         return null;
      } else {
         FlatLayerInfo flatlayerinfo = new FlatLayerInfo(k, block);
         flatlayerinfo.setMinY(p_197526_1_);
         return flatlayerinfo;
      }
   }

   @OnlyIn(Dist.CLIENT)
   private static List<FlatLayerInfo> deserializeLayers(String p_197527_0_) {
      List<FlatLayerInfo> list = Lists.newArrayList();
      String[] astring = p_197527_0_.split(",");
      int i = 0;

      for(String s : astring) {
         FlatLayerInfo flatlayerinfo = deserializeLayer(s, i);
         if (flatlayerinfo == null) {
            return Collections.emptyList();
         }

         list.add(flatlayerinfo);
         i += flatlayerinfo.getLayerCount();
      }

      return list;
   }

   @OnlyIn(Dist.CLIENT)
   public <T> Dynamic<T> func_210834_a(DynamicOps<T> p_210834_1_) {
      T t = p_210834_1_.createList(this.flatLayers.stream().map((p_210837_1_) -> {
         return p_210834_1_.createMap(ImmutableMap.of(p_210834_1_.createString("height"), p_210834_1_.createInt(p_210837_1_.getLayerCount()), p_210834_1_.createString("block"), p_210834_1_.createString(Registry.BLOCK.getKey(p_210837_1_.getLayerMaterial().getBlock()).toString())));
      }));
      T t1 = p_210834_1_.createMap(this.worldFeatures.entrySet().stream().map((p_210833_1_) -> {
         return Pair.of(p_210834_1_.createString(p_210833_1_.getKey().toLowerCase(Locale.ROOT)), p_210834_1_.createMap(p_210833_1_.getValue().entrySet().stream().map((p_210836_1_) -> {
            return Pair.of(p_210834_1_.createString(p_210836_1_.getKey()), p_210834_1_.createString(p_210836_1_.getValue()));
         }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))));
      }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
      return new Dynamic<>(p_210834_1_, p_210834_1_.createMap(ImmutableMap.of(p_210834_1_.createString("layers"), t, p_210834_1_.createString("biome"), p_210834_1_.createString(Registry.BIOME.getKey(this.biomeToUse).toString()), p_210834_1_.createString("structures"), t1)));
   }

   public static FlatGenerationSettings createFlatGenerator(Dynamic<?> settings) {
      FlatGenerationSettings flatgenerationsettings = ChunkGeneratorType.FLAT.createSettings();
      List<Pair<Integer, Block>> list = settings.get("layers").asList((p_210838_0_) -> {
         return Pair.of(p_210838_0_.get("height").asInt(1), getBlock(p_210838_0_.get("block").asString("")));
      });
      if (list.stream().anyMatch((p_211743_0_) -> {
         return p_211743_0_.getSecond() == null;
      })) {
         return getDefaultFlatGenerator();
      } else {
         List<FlatLayerInfo> list1 = list.stream().map((p_211740_0_) -> {
            return new FlatLayerInfo(p_211740_0_.getFirst(), p_211740_0_.getSecond());
         }).collect(Collectors.toList());
         if (list1.isEmpty()) {
            return getDefaultFlatGenerator();
         } else {
            flatgenerationsettings.getFlatLayers().addAll(list1);
            flatgenerationsettings.updateLayers();
            flatgenerationsettings.setBiome(Registry.BIOME.getOrDefault(new ResourceLocation(settings.get("biome").asString(""))));
            settings.get("structures").flatMap(Dynamic::getMapValues).ifPresent((p_211738_1_) -> {
               p_211738_1_.keySet().forEach((p_211739_1_) -> {
                  p_211739_1_.asString().map((p_211742_1_) -> {
                     return flatgenerationsettings.getWorldFeatures().put(p_211742_1_, Maps.newHashMap());
                  });
               });
            });
            return flatgenerationsettings;
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static FlatGenerationSettings createFlatGeneratorFromString(String flatGeneratorSettings) {
      Iterator<String> iterator = Splitter.on(';').split(flatGeneratorSettings).iterator();
      if (!iterator.hasNext()) {
         return getDefaultFlatGenerator();
      } else {
         FlatGenerationSettings flatgenerationsettings = ChunkGeneratorType.FLAT.createSettings();
         List<FlatLayerInfo> list = deserializeLayers(iterator.next());
         if (list.isEmpty()) {
            return getDefaultFlatGenerator();
         } else {
            flatgenerationsettings.getFlatLayers().addAll(list);
            flatgenerationsettings.updateLayers();
            Biome biome = Biomes.PLAINS;
            if (iterator.hasNext()) {
               try {
                  ResourceLocation resourcelocation = new ResourceLocation(iterator.next());
                  biome = Registry.BIOME.getValue(resourcelocation).orElseThrow(() -> {
                     return new IllegalArgumentException("Invalid Biome: " + resourcelocation);
                  });
               } catch (Exception exception) {
                  LOGGER.error("Error while parsing flat world string => {}", (Object)exception.getMessage());
               }
            }

            flatgenerationsettings.setBiome(biome);
            if (iterator.hasNext()) {
               String[] astring3 = iterator.next().toLowerCase(Locale.ROOT).split(",");

               for(String s : astring3) {
                  String[] astring = s.split("\\(", 2);
                  if (!astring[0].isEmpty()) {
                     flatgenerationsettings.addStructure(astring[0]);
                     if (astring.length > 1 && astring[1].endsWith(")") && astring[1].length() > 1) {
                        String[] astring1 = astring[1].substring(0, astring[1].length() - 1).split(" ");

                        for(String s1 : astring1) {
                           String[] astring2 = s1.split("=", 2);
                           if (astring2.length == 2) {
                              flatgenerationsettings.setStructureOption(astring[0], astring2[0], astring2[1]);
                           }
                        }
                     }
                  }
               }
            } else {
               flatgenerationsettings.getWorldFeatures().put("village", Maps.newHashMap());
            }

            return flatgenerationsettings;
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   private void addStructure(String structureIn) {
      Map<String, String> map = Maps.newHashMap();
      this.worldFeatures.put(structureIn, map);
   }

   @OnlyIn(Dist.CLIENT)
   private void setStructureOption(String structureIn, String key, String value) {
      this.worldFeatures.get(structureIn).put(key, value);
      if ("village".equals(structureIn) && "distance".equals(key)) {
         this.villageDistance = MathHelper.getInt(value, this.villageDistance, 9);
      }

      if ("biome_1".equals(structureIn) && "distance".equals(key)) {
         this.biomeFeatureDistance = MathHelper.getInt(value, this.biomeFeatureDistance, 9);
      }

      if ("stronghold".equals(structureIn)) {
         if ("distance".equals(key)) {
            this.strongholdDistance = MathHelper.getInt(value, this.strongholdDistance, 1);
         } else if ("count".equals(key)) {
            this.strongholdCount = MathHelper.getInt(value, this.strongholdCount, 1);
         } else if ("spread".equals(key)) {
            this.strongholdSpread = MathHelper.getInt(value, this.strongholdSpread, 1);
         }
      }

      if ("oceanmonument".equals(structureIn)) {
         if ("separation".equals(key)) {
            this.oceanMonumentSeparation = MathHelper.getInt(value, this.oceanMonumentSeparation, 1);
         } else if ("spacing".equals(key)) {
            this.oceanMonumentSpacing = MathHelper.getInt(value, this.oceanMonumentSpacing, 1);
         }
      }

      if ("endcity".equals(structureIn) && "distance".equals(key)) {
         this.endCityDistance = MathHelper.getInt(value, this.endCityDistance, 1);
      }

      if ("mansion".equals(structureIn) && "distance".equals(key)) {
         this.mansionDistance = MathHelper.getInt(value, this.mansionDistance, 1);
      }

   }

   public static FlatGenerationSettings getDefaultFlatGenerator() {
      FlatGenerationSettings flatgenerationsettings = ChunkGeneratorType.FLAT.createSettings();
      flatgenerationsettings.setBiome(Biomes.PLAINS);
      flatgenerationsettings.getFlatLayers().add(new FlatLayerInfo(1, Blocks.BEDROCK));
      flatgenerationsettings.getFlatLayers().add(new FlatLayerInfo(2, Blocks.DIRT));
      flatgenerationsettings.getFlatLayers().add(new FlatLayerInfo(1, Blocks.GRASS_BLOCK));
      flatgenerationsettings.updateLayers();
      flatgenerationsettings.getWorldFeatures().put("village", Maps.newHashMap());
      return flatgenerationsettings;
   }

   /**
    * True if all generated blocks are air; false if at least one is not air.
    */
   public boolean isAllAir() {
      return this.allAir;
   }

   public BlockState[] getStates() {
      return this.states;
   }

   public void func_214990_a(int p_214990_1_) {
      this.states[p_214990_1_] = null;
   }
}