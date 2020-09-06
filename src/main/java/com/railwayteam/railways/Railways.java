package com.railwayteam.railways;

import com.tterrag.registrate.Registrate;
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
	public static final String VERSION = "0.1.0";
	public static Railways instance;
    private static final Logger LOGGER = LogManager.getLogger(MODID);
    public static ModSetup setup = new ModSetup();
    public static Registrate railwayRegistrar;

    public Railways() {
    	instance = this;
    	
    	ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
      ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);
    	
      FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
      MinecraftForge.EVENT_BUS.register(this);
        
      Config.loadConfig(Config.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MODID + "-client.toml"));
      Config.loadConfig(Config.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MODID + "-common.toml"));
        
      FMLJavaModLoadingContext.get().getModEventBus().addListener(Railways::clientInit);
      railwayRegistrar = Registrate.create(Railways.MODID);
      setup.register(railwayRegistrar);
    }

    private void setup(final FMLCommonSetupEvent event) {
    	setup.init();
    }
    
    public static ResourceLocation createResourceLocation(String name) {
		return new ResourceLocation(MODID, name);
	}
    
    public static void clientInit(FMLClientSetupEvent event) {
        RenderTypeLookup.setRenderLayer(ModSetup.R_BLOCK_WAYPOINT.get(), RenderType.getCutoutMipped());
        setup.registerRenderers();
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
    }
}
