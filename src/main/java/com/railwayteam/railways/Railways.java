package com.railwayteam.railways;

import com.railwayteam.railways.content.entities.conductor.ConductorRenderer;
import com.railwayteam.railways.content.items.StationEditorItem;
import com.railwayteam.railways.content.uiandrendering.Containers;
import com.railwayteam.railways.interaction.RailwaysPacketHandler;
import com.railwayteam.railways.interaction.capabilities.CapabilitySetup;
import com.railwayteam.railways.registry.CRBlocks;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.railwayteam.railways.registry.CREntities.R_ENTITY_CONDUCTOR;

@Mod(Railways.MODID)
public class Railways {
	public static final String MODID = "railways";
	public static final String VERSION = "0.2.0";
	public static Railways instance;
  public static final Logger LOGGER = LogManager.getLogger(MODID);
  public static ModSetup setup = new ModSetup();
  public static CreateRegistrate railwayRegistrar;
  public static IEventBus MOD_EVENT_BUS;

  public Railways() {
  	instance = this;

  	railwayRegistrar = CreateRegistrate.lazy(MODID).get();

  	ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
    ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);

    MOD_EVENT_BUS = FMLJavaModLoadingContext.get().getModEventBus();

    MOD_EVENT_BUS.addListener(this::setup);
    MinecraftForge.EVENT_BUS.register(this);

    Config.loadConfig(Config.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MODID + "-client.toml"));
    Config.loadConfig(Config.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MODID + "-common.toml"));

    MOD_EVENT_BUS.addListener(Railways::clientInit);

    ModSetup.register(railwayRegistrar);

    MOD_EVENT_BUS.register(CapabilitySetup.class);
    MOD_EVENT_BUS.register(StationEditorItem.class);

    MOD_EVENT_BUS.addGenericListener(ContainerType.class, Containers::register);
  }

  private void setup(final FMLCommonSetupEvent event) {
    setup.init();
    RailwaysPacketHandler.register();
  }

  public static ResourceLocation createResourceLocation(String name) {
		return new ResourceLocation(MODID, name);
	}

  public static void clientInit(FMLClientSetupEvent event) {
    RenderTypeLookup.setRenderLayer(CRBlocks.R_BLOCK_WAYPOINT.get(), RenderType.getCutoutMipped());
    RenderTypeLookup.setRenderLayer(CRBlocks.R_BLOCK_LARGE_RAIL.get(), RenderType.getCutoutMipped());
    RenderTypeLookup.setRenderLayer(CRBlocks.R_BLOCK_LARGE_SWITCH.get(), RenderType.getCutoutMipped());
    RenderTypeLookup.setRenderLayer(CRBlocks.R_BLOCK_STATION_SENSOR.get(), RenderType.getTranslucent());
    RenderingRegistry.registerEntityRenderingHandler(R_ENTITY_CONDUCTOR.get(),
            ConductorRenderer::new);
    Containers.registerScreenFactories();
  }

  @SubscribeEvent
  public void onServerStarting(FMLServerStartingEvent event) {
  }
}
