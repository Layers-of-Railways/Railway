/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2025 The Railways Team
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

package com.railwayteam.railways.config;

import net.createmod.catnip.config.ConfigBase;
import net.createmod.catnip.config.ui.ConfigAnnotations;

@SuppressWarnings("unused")
public class CCommon extends ConfigBase {

    public final ConfigBool registerMissingTracks = b(false, "registerMissingTracks", Comments.registerMissingTracks, ConfigAnnotations.RequiresRestart.BOTH.asComment());
    public final ConfigBool disableDatafixer = b(false, "disableDatafixer", Comments.disableDatafixer, ConfigAnnotations.RequiresRestart.BOTH.asComment());

    @Override
    public String getName() {
        return "common";
    }

    private static class Comments {
        static String registerMissingTracks = "Register integration tracks for mods that are not present";
        static String disableDatafixer = "Disable Steam 'n' Rails datafixers. Do not enable this config if your world contains pre-Create 0.5.1 monobogeys, because then they will be destroyed";
    }
}
