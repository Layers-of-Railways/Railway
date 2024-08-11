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

package com.railwayteam.railways.compat.tracks.mods;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.compat.Mods;
import com.railwayteam.railways.compat.tracks.GenericTrackCompat;

public class GarnishedTrackCompat extends GenericTrackCompat {
    GarnishedTrackCompat() {
        super(Mods.GARNISHED);
    }

    private static boolean registered = false;
    public static void register() {
        if (registered) {
            Railways.LOGGER.error("Duplicate registration of Garnished compat");
            return;
        }
        registered = true;
        Railways.LOGGER.info("Registering tracks for Garnished");
        new GarnishedTrackCompat().register(
                "nut",
                "sepia"//,

                // In Create: Garnished v1.9, not currently implemented
                // "zultanite",
                // "red_zultanite",
                // "orange_zultanite",
                // "yellow_zultanite",
                // "green_zultanite",
                // "lime_zultanite",
                // "blue_zultanite",
                // "light_blue_zultanite",
                // "cyan_zultanite",
                // "purple_zultanite",
                // "magenta_zultanite",
                // "pink_zultanite",
                // "black_zultanite",
                // "gray_zultanite",
                // "light_gray_zultanite",
                // "white_zultanite",
                // "brown_zultanite"
        );
    }
}
