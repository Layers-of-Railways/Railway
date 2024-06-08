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

import com.google.common.collect.ImmutableMap;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.compat.Mods;
import com.railwayteam.railways.compat.tracks.GenericTrackCompat;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class QuarkTrackCompat extends GenericTrackCompat {
    QuarkTrackCompat() {
        super(Mods.QUARK);
    }

    private static final Map<String, String> slab_map = ImmutableMap.of(
            "blossom", "blossom_planks_slab",
            "ancient", "ancient_planks_slab",
            "azalea", "azalea_planks_slab"
    );

    private static final Map<String, String> lang_map = ImmutableMap.of(
            "blossom", "Trumpet",
            "ancient", "Ashen"
    );

    @Override
    protected ResourceLocation getSlabLocation(String name) {
        if (slab_map.containsKey(name)) return asResource(slab_map.get(name));
        return super.getSlabLocation(name);
    }

    @Override
    protected String getLang(String name) {
        if (lang_map.containsKey(name))
            return lang_map.get(name);
        return super.getLang(name);
    }

    private static boolean registered = false;
    public static void register() {
        if (registered) {
            Railways.LOGGER.error("Duplicate registration of Quark track compat");
            return;
        }
        registered = true;
        Railways.LOGGER.info("Registering tracks for Quark");
        new QuarkTrackCompat().register(
                "blossom",
                "ancient",
                "azalea"
        );
    }
}
