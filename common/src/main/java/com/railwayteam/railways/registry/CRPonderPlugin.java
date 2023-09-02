package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import net.createmod.ponder.foundation.PonderPlugin;
import org.jetbrains.annotations.NotNull;

public class CRPonderPlugin implements PonderPlugin {
    @Override
    public @NotNull String getModID() {
        return Railways.MODID;
    }

    @Override
    public void registerScenes() {
        CRPonderIndex.register();
    }
}
