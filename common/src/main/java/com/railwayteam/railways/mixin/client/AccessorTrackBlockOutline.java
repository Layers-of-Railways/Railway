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

package com.railwayteam.railways.mixin.client;

import com.simibubi.create.content.trains.track.TrackBlockOutline;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TrackBlockOutline.class)
public interface AccessorTrackBlockOutline {
    @Accessor("LONG_CROSS")
    static VoxelShape getLongCross() {
        throw new AssertionError();
    }

    @Accessor("LONG_ORTHO")
    static VoxelShape getLongOrtho() {
        throw new AssertionError();
    }

    @Accessor("LONG_ORTHO_OFFSET")
    static VoxelShape getLongOrthoOffset() {
        throw new AssertionError();
    }
}
