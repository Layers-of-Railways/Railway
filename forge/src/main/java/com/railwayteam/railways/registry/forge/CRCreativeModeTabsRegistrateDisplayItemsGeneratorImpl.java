package com.railwayteam.railways.registry.forge;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.registry.CRCreativeModeTabs;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.RegistryObject;

public class CRCreativeModeTabsRegistrateDisplayItemsGeneratorImpl {
    public static boolean isInCreativeTab(RegistryEntry<?> entry, ResourceKey<CreativeModeTab> tab) {
        RegistryObject<CreativeModeTab> tabObject;
        if (tab == CRCreativeModeTabs.getBaseTabKey()) {
            tabObject = CRCreativeModeTabsImpl.MAIN_TAB;
        } else if (tab == CRCreativeModeTabs.getCapsTabKey()) {
            tabObject = CRCreativeModeTabsImpl.CAPS_TAB;
        } else if (tab == CRCreativeModeTabs.getCompatTracksTabKey()) {
            tabObject = CRCreativeModeTabsImpl.COMPAT_TAB;
        } else {
            tabObject = CRCreativeModeTabsImpl.MAIN_TAB;
        }
        return Railways.registrate().isInCreativeTab(entry, tabObject);
    }
}
