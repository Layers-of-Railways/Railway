package com.railwayteam.railways.registry.fabric;

import com.railwayteam.railways.Railways;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

public class CRCreativeModeTabsRegistrateDisplayItemsGeneratorImpl {
    public static boolean isInCreativeTab(RegistryEntry<?> entry, ResourceKey<CreativeModeTab> tab) {
        return Railways.registrate().isInCreativeTab(entry, tab);
    }
}
