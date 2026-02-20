/*
 * Steam 'n' Rails
 * Copyright (c) 2026 The Railways Team
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

package com.railwayteam.railways.content.custom_tracks.casing;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;

import static com.railwayteam.railways.registry.CRTags.AllBlockTags.TRACK_CASING_BLACKLIST;
import static com.railwayteam.railways.registry.CRTags.AllBlockTags.TRACK_CASING_WHITELIST;

public class CasingChecker {
    public static boolean isValid(Block block) {
        return (block instanceof SlabBlock || TRACK_CASING_WHITELIST.matches(block)) && !TRACK_CASING_BLACKLIST.matches(block);
    }
}
