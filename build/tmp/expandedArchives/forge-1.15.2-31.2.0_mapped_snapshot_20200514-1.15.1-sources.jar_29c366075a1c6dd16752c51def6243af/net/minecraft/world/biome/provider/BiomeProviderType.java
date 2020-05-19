package net.minecraft.world.biome.provider;

import java.util.function.Function;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.storage.WorldInfo;

public class BiomeProviderType<C extends IBiomeProviderSettings, T extends BiomeProvider> extends net.minecraftforge.registries.ForgeRegistryEntry<BiomeProviderType<?, ?>> {
   public static final BiomeProviderType<CheckerboardBiomeProviderSettings, CheckerboardBiomeProvider> CHECKERBOARD = func_226841_a_("checkerboard", CheckerboardBiomeProvider::new, CheckerboardBiomeProviderSettings::new);
   public static final BiomeProviderType<SingleBiomeProviderSettings, SingleBiomeProvider> FIXED = func_226841_a_("fixed", SingleBiomeProvider::new, SingleBiomeProviderSettings::new);
   public static final BiomeProviderType<OverworldBiomeProviderSettings, OverworldBiomeProvider> VANILLA_LAYERED = func_226841_a_("vanilla_layered", OverworldBiomeProvider::new, OverworldBiomeProviderSettings::new);
   public static final BiomeProviderType<EndBiomeProviderSettings, EndBiomeProvider> THE_END = func_226841_a_("the_end", EndBiomeProvider::new, EndBiomeProviderSettings::new);
   private final Function<C, T> factory;
   private final Function<WorldInfo, C> settingsFactory;

   private static <C extends IBiomeProviderSettings, T extends BiomeProvider> BiomeProviderType<C, T> func_226841_a_(String p_226841_0_, Function<C, T> p_226841_1_, Function<WorldInfo, C> p_226841_2_) {
      return Registry.register(Registry.BIOME_SOURCE_TYPE, p_226841_0_, new BiomeProviderType<>(p_226841_1_, p_226841_2_));
   }

   private BiomeProviderType(Function<C, T> p_i225746_1_, Function<WorldInfo, C> p_i225746_2_) {
      this.factory = p_i225746_1_;
      this.settingsFactory = p_i225746_2_;
   }

   public T create(C settings) {
      return (T)(this.factory.apply(settings));
   }

   public C createSettings(WorldInfo p_226840_1_) {
      return (C)(this.settingsFactory.apply(p_226840_1_));
   }
}