package com.railwayteam.railways.forge;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.registry.forge.CRCreativeModeTabsImpl;

public class ModSetupImpl {
    public static void useBaseTab() {
        Railways.registrate().setCreativeTab(CRCreativeModeTabsImpl.MAIN_TAB);
    }

    public static void useTracksTab() {
        Railways.registrate().setCreativeTab(CRCreativeModeTabsImpl.TRACKS_TAB);
    }

    public static void usePalettesTab() {
        Railways.registrate().setCreativeTab(CRCreativeModeTabsImpl.PALETTES_TAB);
    }
}
