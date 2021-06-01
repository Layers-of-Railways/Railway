package com.railwayteam.railways;

import com.railwayteam.railways.capabilities.CapabilitySetup;
import com.railwayteam.railways.entities.conductor.ConductorRenderer;
import com.railwayteam.railways.items.StationEditorItem;
import com.tterrag.registrate.Registrate;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(Railways.MODID)
public class Railways {
	public static final String MODID = "railways";
	public static final String VERSION = "0.2.0";
	public static Railways instance;
  public static final Logger LOGGER = LogManager.getLogger(MODID);
  public static ModSetup setup = new ModSetup();
  public static Registrate railwayRegistrar;
  public static IEventBus MOD_EVENT_BUS;

  public static RailLineSegmentManager SEGMENT_MANAGER = RailLineSegmentManager.getInstance();

  private RailwaysEventHandler eventHandler;

  public Railways() {
  	instance = this;

  	ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
    ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);

    MOD_EVENT_BUS = FMLJavaModLoadingContext.get().getModEventBus();

    MOD_EVENT_BUS.addListener(this::setup);
    MinecraftForge.EVENT_BUS.register(this);
    eventHandler = new RailwaysEventHandler();
    MinecraftForge.EVENT_BUS.register(eventHandler);

    Config.loadConfig(Config.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MODID + "-client.toml"));
    Config.loadConfig(Config.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MODID + "-common.toml"));

    MOD_EVENT_BUS.addListener(Railways::clientInit);

    railwayRegistrar = Registrate.create(Railways.MODID);
    setup.register(railwayRegistrar);

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
    RenderTypeLookup.setRenderLayer(ModSetup.R_BLOCK_WAYPOINT.get(), RenderType.getCutoutMipped());
    RenderTypeLookup.setRenderLayer(ModSetup.R_BLOCK_LARGE_RAIL.get(), RenderType.getCutoutMipped());
    RenderTypeLookup.setRenderLayer(ModSetup.R_BLOCK_LARGE_SWITCH.get(), RenderType.getCutoutMipped());
    RenderTypeLookup.setRenderLayer(ModSetup.R_BLOCK_STATION_SENSOR.get(), RenderType.getTranslucent());
    RenderingRegistry.registerEntityRenderingHandler(ModSetup.R_ENTITY_CONDUCTOR.get(),
            ConductorRenderer::new);
    setup.registerRenderers();
    Containers.registerScreenFactories();
  }

  @SubscribeEvent
  public void registerCommands (RegisterCommandsEvent rce) {
    rce.getDispatcher().register(Commands.literal("graph")
      .then(Commands.literal("set")
        .then(Commands.argument("position", BlockPosArgument.blockPos())
          .executes(context -> {
            return SEGMENT_MANAGER.addTrack(BlockPosArgument.getBlockPos(context, "position"));
        })))
      .then(Commands.literal("get")
        .then(Commands.argument("position", BlockPosArgument.blockPos())
          .executes(context -> {
            boolean found = SEGMENT_MANAGER.containsTrack(BlockPosArgument.getBlockPos(context, "position"));
            if (context.getSource().getEntity() != null) {
              context.getSource().sendErrorMessage(new StringTextComponent("track was " + (found ? "" : "not ") + "found in Graph"));
            }
            return 1;
        })))
      .executes(context -> {
        context.getSource().sendErrorMessage(new StringTextComponent("usage: graph set|get <BlockPos>"));
        return 1;
      })
    );
  }

  @SubscribeEvent
  public void onServerStarting(FMLServerStartingEvent event) {
  }
}
