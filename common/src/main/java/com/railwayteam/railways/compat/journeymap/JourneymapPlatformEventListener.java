/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
