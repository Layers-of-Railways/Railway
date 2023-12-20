package com.railwayteam.railways.forge;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.registry.forge.CRCreativeModeTabsImpl;

public class ModSetupImpl {
    public static void useBaseTab() {
        Railways.registrate().useCreativeTab(CRCreativeModeTabsImpl.MAIN_TAB);
    }

    public static void useTracksTab() {
        Railways.registrate().useCreativeTab(CRCreativeModeTabsImpl.TRACKS_TAB);
    }

    public static void usePalettesTab() {
        Railways.registrate().useCreativeTab(CRCreativeModeTabsImpl.PALETTES_TAB);
    }
}
