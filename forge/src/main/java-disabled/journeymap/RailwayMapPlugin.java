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

import com.railwayteam.railways.Railways;
import journeymap.client.api.ClientPlugin;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.event.ClientEvent;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

import static journeymap.client.api.event.ClientEvent.Type.MAPPING_STOPPED;

@ClientPlugin
public class RailwayMapPlugin implements IClientPlugin {
    private IClientAPI api;

    @Override
    public void initialize(@NotNull IClientAPI api) {
        this.api = api;
        this.api.subscribe(getModId(), EnumSet.of(MAPPING_STOPPED));
        RailwayMarkerHandler.init(api);
    }

    @Override
    public String getModId() {
        return Railways.MOD_ID;
    }

    @Override
    public void onEvent(@NotNull ClientEvent clientEvent) {
        if (clientEvent.type == MAPPING_STOPPED) {
            this.api.removeAll(getModId());
        }
    }

    private static final JourneymapPlatformEventListener listener = JourneymapPlatformEventListener.create(); // just holding on to this

    public static void load() {
        Railways.LOGGER.info("Loaded JourneyMap plugin");
    }

}
