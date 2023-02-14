package com.railwayteam.railways;

import com.railwayteam.railways.base.data.lang.CRLangPartials;
import com.railwayteam.railways.base.data.recipe.RailwaysSequencedAssemblyRecipeGen;
import com.railwayteam.railways.base.data.recipe.RailwaysStandardRecipeGen;
import com.railwayteam.railways.registry.CRCommands;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.LangMerger;
import com.simibubi.create.foundation.ponder.PonderLocalization;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Railways {
  public static final String MODID = "railways";
  public static final Logger LOGGER = LogManager.getLogger(MODID);
  public static final ModSetup setup = new ModSetup();
  public static final String VERSION = findVersion();

  private static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID);

  public static void init() {
  	ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
    ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG);

    ModSetup.register();
    finalizeRegistrate();

    Config.loadConfig(Config.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MODID + "-client.toml"));
    Config.loadConfig(Config.SERVER_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MODID + "-common.toml"));


  }

  public static ResourceLocation asResource(String name) {
		return new ResourceLocation(MODID, name);
	}

    // TODO ARCH: datagen
  public static void gatherData(DataGenerator gen, boolean client, boolean server) {
    if (server) {
      gen.addProvider(new RailwaysSequencedAssemblyRecipeGen(gen));
      gen.addProvider(new RailwaysStandardRecipeGen(gen));
    }
    if (client) {
      PonderLocalization.provideRegistrateLang(REGISTRATE);
      gen.addProvider(new LangMerger(gen, MODID, "Steam 'n Rails", CRLangPartials.values()));
    }

  }

  @SubscribeEvent
  public void registerCommands(RegisterCommandsEvent event) {
    CRCommands.register(event.getDispatcher());
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
}
