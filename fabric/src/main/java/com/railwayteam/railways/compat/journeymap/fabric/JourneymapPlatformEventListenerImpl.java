package com.railwayteam.railways.compat.journeymap.fabric;

import com.railwayteam.railways.compat.journeymap.JourneymapPlatformEventListener;
import journeymap.client.api.event.fabric.FabricEvents;
import journeymap.client.api.event.fabric.FullscreenDisplayEvent;

public class JourneymapPlatformEventListenerImpl extends JourneymapPlatformEventListener {
    public static JourneymapPlatformEventListener create() {
        JourneymapPlatformEventListener listener = new JourneymapPlatformEventListenerImpl();
        listener.register();
        return listener;
    }

    @Override
    public void register() {
        FabricEvents.ADDON_BUTTON_DISPLAY_EVENT.register(this::onAddonButtonDisplayEvent);
    }

    private void onAddonButtonDisplayEvent(FullscreenDisplayEvent.AddonButtonDisplayEvent event) {
        onAddonButtonDisplay(event.getThemeButtonDisplay());
    }
}
