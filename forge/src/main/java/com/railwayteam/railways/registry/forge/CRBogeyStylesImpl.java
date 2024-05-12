package com.railwayteam.railways.registry.forge;

import com.railwayteam.railways.api.bogeymenu.v0.forge.BogeyMenuEvents;
import net.minecraftforge.common.MinecraftForge;

public class CRBogeyStylesImpl {
    public static void fireReadyForRegistrationEvent() {
        MinecraftForge.EVENT_BUS.post(new BogeyMenuEvents.EntryRegistrationEvent());
    }
}
