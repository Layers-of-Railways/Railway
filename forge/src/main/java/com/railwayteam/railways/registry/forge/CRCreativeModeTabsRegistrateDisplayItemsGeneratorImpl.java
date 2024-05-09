package com.railwayteam.railways.registry.forge;

import com.railwayteam.railways.registry.CRCreativeModeTabs;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.RegistryObject;

public class CRCreativeModeTabsRegistrateDisplayItemsGeneratorImpl {
    public static boolean isInCreativeTab(RegistryEntry<?> entry, ResourceKey<CreativeModeTab> tab) {
        RegistryObject<CreativeModeTab> tabObject;
        if (tab == CRCreativeModeTabs.getBaseTabKey()) {
            tabObject = CRCreativeModeTabsImpl.MAIN_TAB;
        } else if (tab == CRCreativeModeTabs.getTracksTabKey()) {
            tabObject = CRCreativeModeTabsImpl.TRACKS_TAB;
        } else if (tab == CRCreativeModeTabs.getPalettesTabKey()) {
            tabObject = CRCreativeModeTabsImpl.PALETTES_TAB;
        } else {
            tabObject = CRCreativeModeTabsImpl.MAIN_TAB;
        }
        return CreateRegistrate.isInCreativeTab(entry, tabObject);
    }
}
