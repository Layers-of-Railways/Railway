package com.railwayteam.railways;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
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

    public Railways() {
    	instance = this;
    	
    	ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);
    	
    	
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
        
        Config.loadConfig(Config.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MODID + "-client.toml"));
        Config.loadConfig(Config.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MODID + "-common.toml"));
    }

    private void setup(final FMLCommonSetupEvent event) {
    	setup.init();
    	
    	
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }

    
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }

    
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
    	@SubscribeEvent
        @SuppressWarnings("unused")
        public static void registerItems(final RegistryEvent.Register<Item> event) {
            LOGGER.info("items registering");
            Item.Properties properties = new Item.Properties().group(ModSetup.itemGroup);

            // event.getRegistry().register(new BlockItem(RegistryEntries.DYNAMO, properties).setRegistryName("dynamo")); //   EXAMPLE!
            
            LOGGER.info("finished items registering");
        }

        @SubscribeEvent
        @SuppressWarnings("unused")
        public static void registerBlocks(final RegistryEvent.Register<Block> event) {
            LOGGER.info("blocks registering");
            // event.getRegistry().register(new Dynamo());	// EXAMPLE!
            LOGGER.info("finished blocks registering");

        }


        @SubscribeEvent
        @SuppressWarnings("unused")
        public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> event) {
            LOGGER.info("TEs registering");
            // event.getRegistry().register(TileEntityType.Builder.create(DynamoTile::new, RegistryEntries.DYNAMO).build(null).setRegistryName("dynamo"));	//EXAMPLE
            LOGGER.info("finished TEs registering");
        }


        @SubscribeEvent
        @SuppressWarnings("unused")
        public static void onContainerRegistry(final RegistryEvent.Register<ContainerType<?>> event) {
            /*event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new EnderContainer(windowId, CreateIntegration.proxy.getClientWorld(), pos, inv);
            }).setRegistryName("ender_crate"));*/ 	// EXAMPLE!
        }
    }
}
