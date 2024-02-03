package com.railwayteam.railways.fabric;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.registry.CRCreativeModeTabs;

public class ModSetupImpl {
    public static void useBaseTab() {
        Railways.registrate().setCreativeTab(CRCreativeModeTabs.getBaseTabKey());
    }

    public static void useTracksTab() {
        Railways.registrate().setCreativeTab(CRCreativeModeTabs.getTracksTabKey());
    }

    public static void usePalettesTab() {
        Railways.registrate().setCreativeTab(CRCreativeModeTabs.getPalettesTabKey());
    }
}
