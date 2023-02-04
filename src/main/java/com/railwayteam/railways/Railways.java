package com.railwayteam.railways;

import com.railwayteam.railways.base.data.lang.CRLangPartials;
import com.railwayteam.railways.base.data.recipe.RailwaysSequencedAssemblyRecipeGen;
import com.railwayteam.railways.base.data.recipe.RailwaysStandardRecipeGen;
import com.railwayteam.railways.content.conductor.ConductorCapModel;
import com.railwayteam.railways.content.conductor.ConductorEntityModel;
import com.railwayteam.railways.registry.CRCommands;
import com.railwayteam.railways.util.packet.PacketSender;
import com.simibubi.create.foundation.ModFilePackResources;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.LangMerger;
import com.simibubi.create.foundation.ponder.PonderLocalization;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.MavenVersionStringHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.locating.IModFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@Mod(Railways.MODID)
public class Railways {
  public static final String MODID = "railways";
  public static Railways instance;
  public static final Logger LOGGER = LogManager.getLogger(MODID);
  public static final ModSetup setup = new ModSetup();
  public static final String VERSION = getVersion();

  private static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID);

  public static IEventBus MOD_EVENT_BUS;

  public Railways() {
  	instance = this;

  	ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
    ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG);

    MOD_EVENT_BUS = FMLJavaModLoadingContext.get().getModEventBus();

    ModSetup.register();


    REGISTRATE.registerEventListeners(MOD_EVENT_BUS);
    MOD_EVENT_BUS.addListener(this::setup);
    MOD_EVENT_BUS.addListener(EventPriority.LOWEST, Railways::gatherData);
    MOD_EVENT_BUS.addListener(this::registerModelLayers);
    MOD_EVENT_BUS.addListener(this::addPackFinders);
    MinecraftForge.EVENT_BUS.register(this);

    Config.loadConfig(Config.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MODID + "-client.toml"));
    Config.loadConfig(Config.SERVER_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MODID + "-common.toml"));

    MOD_EVENT_BUS.addListener(RailwaysClient::clientSetup);

    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> RailwaysClient::clientCtor);
  }

  private void setup(final FMLCommonSetupEvent event) {
    setup.init();
  }

  public static ResourceLocation asResource(String name) {
		return new ResourceLocation(MODID, name);
	}

  public static void gatherData(GatherDataEvent event) {
    DataGenerator gen = event.getGenerator();
    if (event.includeServer()) {
      gen.addProvider(true, new RailwaysSequencedAssemblyRecipeGen(gen));
      gen.addProvider(true, new RailwaysStandardRecipeGen(gen));
    }
    if (event.includeClient()) {
      PonderLocalization.provideRegistrateLang(REGISTRATE);
      gen.addProvider(true, new LangMerger(gen, MODID, "Steam 'n Rails", CRLangPartials.values()));
    }

  }

  //Thanks to Create for this event
  public void addPackFinders(AddPackFindersEvent event) {
    if (event.getPackType() == PackType.CLIENT_RESOURCES) {
      IModFileInfo modFileInfo = ModList.get().getModFileById(Railways.MODID);
      if (modFileInfo == null) {
        Railways.LOGGER.error("Could not find Steam & Rails mod file info; built-in resource packs will be missing!");
        return;
      }
      IModFile modFile = modFileInfo.getFile();
      event.addRepositorySource((consumer, constructor) -> consumer.accept(Pack.create(Railways.asResource("legacy_semaphore").toString(),
          false, () -> new ModFilePackResources("Steam 'n Rails Legacy Semaphores", modFile, "resourcepacks/legacy_semaphore"),
          constructor, Pack.Position.TOP, PackSource.DEFAULT)));
      event.addRepositorySource((consumer, constructor) -> consumer.accept(Pack.create(Railways.asResource("green_signals").toString(),
          false, () -> new ModFilePackResources("Steam 'n Rails Green Signals", modFile, "resourcepacks/green_signals"),
          constructor, Pack.Position.TOP, PackSource.DEFAULT)));
    }
  }

  @SubscribeEvent
  public void registerModelLayers (EntityRenderersEvent.RegisterLayerDefinitions event) {
    event.registerLayerDefinition(ConductorEntityModel.LAYER_LOCATION, ConductorEntityModel::createBodyLayer);
    event.registerLayerDefinition(ConductorCapModel.LAYER_LOCATION, ConductorCapModel::createBodyLayer);
  }

  @SubscribeEvent
  public void registerCommands(RegisterCommandsEvent event) {
    CRCommands.register(event.getDispatcher());
  }

  @SubscribeEvent
  public void registerClientCommands(RegisterClientCommandsEvent event) {
    CRCommands.registerClient(event.getDispatcher());
  }

  @SubscribeEvent
  public void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
    if (event.getEntity() instanceof ServerPlayer serverPlayer) {
      PacketSender.notifyServerVersion(() -> serverPlayer);
    }
  }

  public static CreateRegistrate registrate() {
    return REGISTRATE;
  }

  private static String getVersion() {
    String versionString = "UNKNOWN";

    List<IModInfo> infoList = ModList.get().getModFileById(MODID).getMods();
    if (infoList.size() > 1) {
      LOGGER.error("Multiple mods for MOD_ID: "+MODID);
    }
    for (IModInfo info : infoList) {
      if (info.getModId().equals(MODID)) {
        versionString = MavenVersionStringHelper.artifactVersionToString(info.getVersion());
        break;
      }
    }
    return versionString;
  }
}
