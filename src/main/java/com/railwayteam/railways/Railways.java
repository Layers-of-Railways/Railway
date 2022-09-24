package com.railwayteam.railways;

import com.railwayteam.railways.content.Conductor.ConductorCapModel;
import com.railwayteam.railways.content.Conductor.ConductorEntityModel;
import com.railwayteam.railways.registry.CRBlockPartials;
import com.tterrag.registrate.Registrate;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Railways.MODID)
public class Railways {
	public static final String MODID = "railways";
	public static final String VERSION = "0.2.0";
	public static Railways instance;
  public static final Logger LOGGER = LogManager.getLogger(MODID);
  public static ModSetup setup = new ModSetup();
  public static Registrate railwayRegistrar;
  public static IEventBus MOD_EVENT_BUS;

  public Railways() {
  	instance = this;

  	railwayRegistrar = Registrate.create(MODID);

  	ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
    ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);

    MOD_EVENT_BUS = FMLJavaModLoadingContext.get().getModEventBus();

    MOD_EVENT_BUS.addListener(this::setup);
    MOD_EVENT_BUS.addListener(this::registerModelLayers);
    MinecraftForge.EVENT_BUS.register(this);

    Config.loadConfig(Config.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MODID + "-client.toml"));
    Config.loadConfig(Config.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MODID + "-common.toml"));

    MOD_EVENT_BUS.addListener(Railways::clientInit);

    ModSetup.register(railwayRegistrar);
    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> CRBlockPartials::init);
  }

  private void setup(final FMLCommonSetupEvent event) {
    setup.init();
  }

  public static ResourceLocation createResourceLocation(String name) {
		return new ResourceLocation(MODID, name);
	}

  public static void clientInit(FMLClientSetupEvent event) {
  }

  @SubscribeEvent
  public void registerModelLayers (EntityRenderersEvent.RegisterLayerDefinitions event) {
    event.registerLayerDefinition(ConductorEntityModel.LAYER_LOCATION, ConductorEntityModel::createBodyLayer);
    event.registerLayerDefinition(ConductorCapModel.LAYER_LOCATION, ConductorCapModel::createBodyLayer);
  }
}
