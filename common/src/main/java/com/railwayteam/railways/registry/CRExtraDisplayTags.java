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

package com.railwayteam.railways.registry;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.ponder.PonderRegistry;
import com.simibubi.create.infrastructure.ponder.AllPonderTags;

public class CRExtraDisplayTags {
    public static void register() {
        PonderRegistry.TAGS.forTag(AllPonderTags.DISPLAY_SOURCES)
                .add(AllBlocks.TRACK_SIGNAL)
                .add(CRBlocks.TRACK_COUPLER)
                .add(CRBlocks.ANDESITE_SWITCH)
                .add(CRBlocks.BRASS_SWITCH);
        PonderRegistry.TAGS.forTag(AllPonderTags.DISPLAY_TARGETS)
                .add(CRBlocks.SEMAPHORE);
    }
}
