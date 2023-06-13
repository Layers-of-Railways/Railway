package com.railwayteam.railways;

import com.mojang.brigadier.CommandDispatcher;
import com.railwayteam.railways.base.data.CRTagGen;
import com.railwayteam.railways.base.data.lang.CRLangPartials;
import com.railwayteam.railways.base.data.recipe.RailwaysSequencedAssemblyRecipeGen;
import com.railwayteam.railways.base.data.recipe.RailwaysStandardRecipeGen;
import com.railwayteam.railways.compat.Mods;
import com.railwayteam.railways.registry.CRCommands;
import com.railwayteam.railways.registry.CRItems;
import com.railwayteam.railways.registry.CRPackets;
import com.railwayteam.railways.util.Utils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.LangMerger;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.simibubi.create.foundation.ponder.PonderLocalization;
import com.tterrag.registrate.providers.ProviderType;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.Type;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.MixinEnvironment;

import java.nio.file.Path;
import java.util.function.BiConsumer;

public class Railways {
  public static final String MODID = "railways";
  public static final Logger LOGGER = LogManager.getLogger(MODID);
  public static final String VERSION = findVersion();
  public static final int DATA_FIXER_VERSION = 1; // Only used for datafixers, bump whenever a block changes id etc (should not be bumped multiple times within a release)

  private static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID)
          .creativeModeTab(() -> CRItems.mainCreativeTab, "Create Steam 'n Rails");

  static {
    REGISTRATE.setTooltipModifierFactory(item -> {
      return new ItemDescription.Modifier(item, TooltipHelper.Palette.STANDARD_CREATE)
          .andThen(TooltipModifier.mapNull(KineticStats.create(item)));
    });
  }

  public static void init() {
    ModSetup.register();
    finalizeRegistrate();

    registerConfig(Type.CLIENT, Config.CLIENT_CONFIG);
    registerConfig(Type.SERVER, Config.SERVER_CONFIG);
    Path configDir = Utils.configDir();
    Config.loadConfig(Config.CLIENT_CONFIG, configDir.resolve(MODID + "-client.toml"));
    Config.loadConfig(Config.SERVER_CONFIG, configDir.resolve(MODID + "-common.toml"));

    registerCommands(CRCommands::register);
    CRPackets.PACKETS.registerC2SListener();

/*    RegistrationListening.whenBothRegistered(
            Registry.BLOCK_ENTITY_TYPE, new ResourceLocation("create", "track"),
            Registry.BLOCK, CRBlocks.MONORAIL_TRACK.getId(), // last track
            (type, block) -> TrackMaterial.addCustomValidTracks(type)
    );*/

    if (Utils.isDevEnv() && !Mods.BYG.isLoaded) // force all mixins to load in dev
      MixinEnvironment.getCurrentEnvironment().audit();
  }

  public static ResourceLocation asResource(String name) {
    return new ResourceLocation(MODID, name);
  }

  public static void gatherData(DataGenerator gen) {
    REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, CRTagGen::generateBlockTags);
    REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, CRTagGen::generateItemTags);
    gen.addProvider(true, RailwaysSequencedAssemblyRecipeGen.create(gen));
    gen.addProvider(true, RailwaysStandardRecipeGen.create(gen));
    PonderLocalization.provideRegistrateLang(REGISTRATE);
    gen.addProvider(true, new LangMerger(gen, MODID, "Steam 'n Rails", CRLangPartials.values()));
  }

  public static CreateRegistrate registrate() {
    return REGISTRATE;
  }

  @ExpectPlatform
  public static String findVersion() {
    throw new AssertionError();
  }

  @ExpectPlatform
  public static void finalizeRegistrate() {
    throw new AssertionError();
  }

  @ExpectPlatform
  public static void registerCommands(BiConsumer<CommandDispatcher<CommandSourceStack>, Boolean> consumer) {
    throw new AssertionError();
  }

  @ExpectPlatform
  public static void registerConfig(ModConfig.Type type, ForgeConfigSpec spec) {
    throw new AssertionError();
  }
}
