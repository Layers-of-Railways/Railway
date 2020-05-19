package net.minecraft.world.gen.layer;

import java.util.function.LongFunction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.LazyAreaLayerContext;
import net.minecraft.world.gen.OverworldGenSettings;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer1;

public class LayerUtil {
   protected static final int WARM_OCEAN = Registry.BIOME.getId(Biomes.WARM_OCEAN);
   protected static final int LUKEWARM_OCEAN = Registry.BIOME.getId(Biomes.LUKEWARM_OCEAN);
   protected static final int OCEAN = Registry.BIOME.getId(Biomes.OCEAN);
   protected static final int COLD_OCEAN = Registry.BIOME.getId(Biomes.COLD_OCEAN);
   protected static final int FROZEN_OCEAN = Registry.BIOME.getId(Biomes.FROZEN_OCEAN);
   protected static final int DEEP_WARM_OCEAN = Registry.BIOME.getId(Biomes.DEEP_WARM_OCEAN);
   protected static final int DEEP_LUKEWARM_OCEAN = Registry.BIOME.getId(Biomes.DEEP_LUKEWARM_OCEAN);
   protected static final int DEEP_OCEAN = Registry.BIOME.getId(Biomes.DEEP_OCEAN);
   protected static final int DEEP_COLD_OCEAN = Registry.BIOME.getId(Biomes.DEEP_COLD_OCEAN);
   protected static final int DEEP_FROZEN_OCEAN = Registry.BIOME.getId(Biomes.DEEP_FROZEN_OCEAN);

   public static <T extends IArea, C extends IExtendedNoiseRandom<T>> IAreaFactory<T> repeat(long seed, IAreaTransformer1 parent, IAreaFactory<T> p_202829_3_, int count, LongFunction<C> contextFactory) {
      IAreaFactory<T> iareafactory = p_202829_3_;

      for(int i = 0; i < count; ++i) {
         iareafactory = parent.apply(contextFactory.apply(seed + (long)i), iareafactory);
      }

      return iareafactory;
   }

   public static <T extends IArea, C extends IExtendedNoiseRandom<T>> IAreaFactory<T> func_227475_a_(WorldType p_227475_0_, OverworldGenSettings p_227475_1_, LongFunction<C> p_227475_2_) {
      IAreaFactory<T> iareafactory = IslandLayer.INSTANCE.apply(p_227475_2_.apply(1L));
      iareafactory = ZoomLayer.FUZZY.apply(p_227475_2_.apply(2000L), iareafactory);
      iareafactory = AddIslandLayer.INSTANCE.apply(p_227475_2_.apply(1L), iareafactory);
      iareafactory = ZoomLayer.NORMAL.apply(p_227475_2_.apply(2001L), iareafactory);
      iareafactory = AddIslandLayer.INSTANCE.apply(p_227475_2_.apply(2L), iareafactory);
      iareafactory = AddIslandLayer.INSTANCE.apply(p_227475_2_.apply(50L), iareafactory);
      iareafactory = AddIslandLayer.INSTANCE.apply(p_227475_2_.apply(70L), iareafactory);
      iareafactory = RemoveTooMuchOceanLayer.INSTANCE.apply(p_227475_2_.apply(2L), iareafactory);
      IAreaFactory<T> iareafactory1 = OceanLayer.INSTANCE.apply(p_227475_2_.apply(2L));
      iareafactory1 = repeat(2001L, ZoomLayer.NORMAL, iareafactory1, 6, p_227475_2_);
      iareafactory = AddSnowLayer.INSTANCE.apply(p_227475_2_.apply(2L), iareafactory);
      iareafactory = AddIslandLayer.INSTANCE.apply(p_227475_2_.apply(3L), iareafactory);
      iareafactory = EdgeLayer.CoolWarm.INSTANCE.apply(p_227475_2_.apply(2L), iareafactory);
      iareafactory = EdgeLayer.HeatIce.INSTANCE.apply(p_227475_2_.apply(2L), iareafactory);
      iareafactory = EdgeLayer.Special.INSTANCE.apply(p_227475_2_.apply(3L), iareafactory);
      iareafactory = ZoomLayer.NORMAL.apply(p_227475_2_.apply(2002L), iareafactory);
      iareafactory = ZoomLayer.NORMAL.apply(p_227475_2_.apply(2003L), iareafactory);
      iareafactory = AddIslandLayer.INSTANCE.apply(p_227475_2_.apply(4L), iareafactory);
      iareafactory = AddMushroomIslandLayer.INSTANCE.apply(p_227475_2_.apply(5L), iareafactory);
      iareafactory = DeepOceanLayer.INSTANCE.apply(p_227475_2_.apply(4L), iareafactory);
      iareafactory = repeat(1000L, ZoomLayer.NORMAL, iareafactory, 0, p_227475_2_);
      int i = p_227475_0_ == WorldType.LARGE_BIOMES ? 6 : p_227475_1_.getBiomeSize();
      i = getModdedBiomeSize(p_227475_0_, i);
      int j = p_227475_1_.getRiverSize();
      IAreaFactory<T> lvt_7_1_ = repeat(1000L, ZoomLayer.NORMAL, iareafactory, 0, p_227475_2_);
      lvt_7_1_ = StartRiverLayer.INSTANCE.apply((IExtendedNoiseRandom)p_227475_2_.apply(100L), lvt_7_1_);
      IAreaFactory<T> lvt_8_1_ = p_227475_0_.getBiomeLayer(iareafactory, p_227475_1_, p_227475_2_);
      IAreaFactory<T> lvt_9_1_ = repeat(1000L, ZoomLayer.NORMAL, lvt_7_1_, 2, p_227475_2_);
      lvt_8_1_ = HillsLayer.INSTANCE.apply((IExtendedNoiseRandom)p_227475_2_.apply(1000L), lvt_8_1_, lvt_9_1_);
      lvt_7_1_ = repeat(1000L, ZoomLayer.NORMAL, lvt_7_1_, 2, p_227475_2_);
      lvt_7_1_ = repeat(1000L, ZoomLayer.NORMAL, lvt_7_1_, j, p_227475_2_);
      lvt_7_1_ = RiverLayer.INSTANCE.apply((IExtendedNoiseRandom)p_227475_2_.apply(1L), lvt_7_1_);
      lvt_7_1_ = SmoothLayer.INSTANCE.apply((IExtendedNoiseRandom)p_227475_2_.apply(1000L), lvt_7_1_);
      lvt_8_1_ = RareBiomeLayer.INSTANCE.apply((IExtendedNoiseRandom)p_227475_2_.apply(1001L), lvt_8_1_);

      for(int k = 0; k < i; ++k) {
         lvt_8_1_ = ZoomLayer.NORMAL.apply((IExtendedNoiseRandom)p_227475_2_.apply((long)(1000 + k)), lvt_8_1_);
         if (k == 0) {
            lvt_8_1_ = AddIslandLayer.INSTANCE.apply((IExtendedNoiseRandom)p_227475_2_.apply(3L), lvt_8_1_);
         }

         if (k == 1 || i == 1) {
            lvt_8_1_ = ShoreLayer.INSTANCE.apply((IExtendedNoiseRandom)p_227475_2_.apply(1000L), lvt_8_1_);
         }
      }

      lvt_8_1_ = SmoothLayer.INSTANCE.apply((IExtendedNoiseRandom)p_227475_2_.apply(1000L), lvt_8_1_);
      lvt_8_1_ = MixRiverLayer.INSTANCE.apply((IExtendedNoiseRandom)p_227475_2_.apply(100L), lvt_8_1_, lvt_7_1_);
      lvt_8_1_ = MixOceansLayer.INSTANCE.apply(p_227475_2_.apply(100L), lvt_8_1_, iareafactory1);
      return lvt_8_1_;
   }

   public static Layer func_227474_a_(long p_227474_0_, WorldType p_227474_2_, OverworldGenSettings p_227474_3_) {
      int i = 25;
      IAreaFactory<LazyArea> iareafactory = func_227475_a_(p_227474_2_, p_227474_3_, (p_227473_2_) -> {
         return new LazyAreaLayerContext(25, p_227474_0_, p_227473_2_);
      });
      return new Layer(iareafactory);
   }

   public static boolean areBiomesSimilar(int p_202826_0_, int p_202826_1_) {
      if (p_202826_0_ == p_202826_1_) {
         return true;
      } else {
         Biome biome = Registry.BIOME.getByValue(p_202826_0_);
         Biome biome1 = Registry.BIOME.getByValue(p_202826_1_);
         if (biome != null && biome1 != null) {
            if (biome != Biomes.WOODED_BADLANDS_PLATEAU && biome != Biomes.BADLANDS_PLATEAU) {
               if (biome.getCategory() != Biome.Category.NONE && biome1.getCategory() != Biome.Category.NONE && biome.getCategory() == biome1.getCategory()) {
                  return true;
               } else {
                  return biome == biome1;
               }
            } else {
               return biome1 == Biomes.WOODED_BADLANDS_PLATEAU || biome1 == Biomes.BADLANDS_PLATEAU;
            }
         } else {
            return false;
         }
      }
   }

   /* ======================================== FORGE START =====================================*/
   public static int getModdedBiomeSize(WorldType worldType, int original)
   {
       net.minecraftforge.event.terraingen.WorldTypeEvent.BiomeSize event = new net.minecraftforge.event.terraingen.WorldTypeEvent.BiomeSize(worldType, original);
       net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
       return event.getNewSize();
   }
   /* ========================================= FORGE END ======================================*/

   protected static boolean isOcean(int biomeIn) {
      return biomeIn == WARM_OCEAN || biomeIn == LUKEWARM_OCEAN || biomeIn == OCEAN || biomeIn == COLD_OCEAN || biomeIn == FROZEN_OCEAN || biomeIn == DEEP_WARM_OCEAN || biomeIn == DEEP_LUKEWARM_OCEAN || biomeIn == DEEP_OCEAN || biomeIn == DEEP_COLD_OCEAN || biomeIn == DEEP_FROZEN_OCEAN;
   }

   protected static boolean isShallowOcean(int biomeIn) {
      return biomeIn == WARM_OCEAN || biomeIn == LUKEWARM_OCEAN || biomeIn == OCEAN || biomeIn == COLD_OCEAN || biomeIn == FROZEN_OCEAN;
   }
}