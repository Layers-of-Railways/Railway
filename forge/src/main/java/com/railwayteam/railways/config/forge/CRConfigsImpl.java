package com.railwayteam.railways.config.forge;

import com.railwayteam.railways.config.CRConfigs;
import com.simibubi.create.foundation.config.ConfigBase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.Map;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CRConfigsImpl {
    public static void register(ModLoadingContext context) {
        CRConfigs.registerCommon();

        for (Map.Entry<ModConfig.Type, ConfigBase> pair : CRConfigs.CONFIGS.entrySet())
            context.registerConfig(pair.getKey(), pair.getValue().specification);
    }

    @SubscribeEvent
    public static void onLoad(ModConfigEvent.Loading event) {
        for (ConfigBase config : CRConfigs.CONFIGS.values())
            if (config.specification == event.getConfig()
                .getSpec())
                config.onLoad();
    }

    @SubscribeEvent
    public static void onReload(ModConfigEvent.Reloading event) {
        for (ConfigBase config : CRConfigs.CONFIGS.values())
            if (config.specification == event.getConfig()
                .getSpec())
                config.onReload();
    }
}
