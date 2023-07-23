package com.railwayteam.railways.forge;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.registry.forge.CRCreativeModeTabsImpl;

public class ModSetupImpl {
    public static void useBaseTab() {
        Railways.registrate().useCreativeTab(CRCreativeModeTabsImpl.MAIN_TAB);
    }

    public static void useCompatTab() {
        Railways.registrate().useCreativeTab(CRCreativeModeTabsImpl.COMPAT_TAB);
    }
}
