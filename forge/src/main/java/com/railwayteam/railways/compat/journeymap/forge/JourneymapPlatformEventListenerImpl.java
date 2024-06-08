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
