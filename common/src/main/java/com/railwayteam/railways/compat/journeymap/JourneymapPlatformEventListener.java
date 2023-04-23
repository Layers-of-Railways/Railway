package com.railwayteam.railways.compat.journeymap;

import dev.architectury.injectables.annotations.ExpectPlatform;
import journeymap.client.api.display.ThemeButtonDisplay;

public abstract class JourneymapPlatformEventListener implements IJourneymapPlatformEventListener {
    protected void onAddonButtonDisplay(ThemeButtonDisplay buttonDisplay) {
        buttonDisplay.addThemeToggleButton(
            "railways.journeymap.train_marker_toggle",
            "journeymap_train",
            DummyRailwayMarkerHandler.getInstance().isEnabled(),
            (button) -> {
                if (!DummyRailwayMarkerHandler.getInstance().isEnabled()) {
                    DummyRailwayMarkerHandler.getInstance().enable();
                } else {
                    DummyRailwayMarkerHandler.getInstance().disable();
                }
            }
        );
    }

    @ExpectPlatform
    public static JourneymapPlatformEventListener create() {
        throw new AssertionError();
    }
}
