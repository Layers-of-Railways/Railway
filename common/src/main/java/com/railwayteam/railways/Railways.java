package com.railwayteam.railways;

import com.mojang.brigadier.CommandDispatcher;
import com.railwayteam.railways.base.data.lang.CRLangPartials;
import com.railwayteam.railways.base.data.recipe.RailwaysSequencedAssemblyRecipeGen;
import com.railwayteam.railways.base.data.recipe.RailwaysStandardRecipeGen;
import com.railwayteam.railways.content.custom_tracks.TrackMaterial;
import com.railwayteam.railways.registry.CRBlocks;
import com.railwayteam.railways.registry.CRCommands;
import com.railwayteam.railways.registry.CRItems;
import com.railwayteam.railways.registry.CRPackets;
import com.railwayteam.railways.util.RegistrationListening;
import com.railwayteam.railways.util.Utils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.LangMerger;
import com.simibubi.create.foundation.ponder.PonderLocalization;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
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
  public static final ModSetup setup = new ModSetup();
  public static final String VERSION = findVersion();

  private static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID).creativeModeTab(() -> CRItems.itemGroup);

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

    RegistrationListening.whenBothRegistered(
            Registry.BLOCK_ENTITY_TYPE, new ResourceLocation("create", "track"),
            Registry.BLOCK, CRBlocks.MONORAIL_TRACK.getId(), // last track
            (type, block) -> TrackMaterial.addCustomValidTracks(type)
    );

    if (Utils.isDevEnv()) // force all mixins to load in dev
      MixinEnvironment.getCurrentEnvironment().audit();
  }

  public static ResourceLocation asResource(String name) {
		return new ResourceLocation(MODID, name);
	}

    // TODO ARCH: datagen
  public static void gatherData(DataGenerator gen, boolean client, boolean server) {
    if (server) {
      gen.addProvider(RailwaysSequencedAssemblyRecipeGen.create(gen));
      gen.addProvider(RailwaysStandardRecipeGen.create(gen));
    }
    if (client) {
      PonderLocalization.provideRegistrateLang(REGISTRATE);
      gen.addProvider(new LangMerger(gen, MODID, "Steam 'n Rails", CRLangPartials.values()));
    }

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
