package com.railwayteam.railways.fabric;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.registry.CRCreativeModeTabs;

public class ModSetupImpl {
    public static void useBaseTab() {
        Railways.registrate().useCreativeTab(CRCreativeModeTabs.getBaseTabKey());
    }

    public static void useTracksTab() {
        Railways.registrate().useCreativeTab(CRCreativeModeTabs.getTracksTabKey());
    }
}
