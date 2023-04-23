package com.railwayteam.railways.compat.journeymap.forge;

import com.railwayteam.railways.compat.journeymap.JourneymapPlatformEventListener;
import journeymap.client.api.event.forge.FullscreenDisplayEvent;
import net.minecraftforge.common.MinecraftForge;

public class JourneymapPlatformEventListenerImpl extends JourneymapPlatformEventListener {

    public static JourneymapPlatformEventListener create() {
        JourneymapPlatformEventListener listener = new JourneymapPlatformEventListenerImpl();
        listener.register();
        return listener;
    }

    @Override
    public void register() {
        MinecraftForge.EVENT_BUS.addListener(this::onAddonButtonDisplayEvent);
    }

    private void onAddonButtonDisplayEvent(FullscreenDisplayEvent.AddonButtonDisplayEvent event) {
        onAddonButtonDisplay(event.getThemeButtonDisplay());
    }
}
