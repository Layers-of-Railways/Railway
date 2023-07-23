package com.railwayteam.railways.registry.forge;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.registry.CRCreativeModeTabs;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

public class CRCreativeModeTabsRegistrateDisplayItemsGeneratorImpl {
    public static boolean isInCreativeTab(RegistryEntry<?> entry, ResourceKey<CreativeModeTab> tab) {
        return Railways.registrate().isInCreativeTab(entry, tab == CRCreativeModeTabs.getBaseTabKey() ? CRCreativeModeTabsImpl.MAIN_TAB : CRCreativeModeTabsImpl.COMPAT_TAB);
    }
}
