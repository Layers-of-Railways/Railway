/*
 * Steam 'n' Rails
 * Copyright (c) 2025 The Railways Team
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

package com.railwayteam.railways.content.custom_bogeys.renderer.unified;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.Affine;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.render.SpriteShiftEntry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public interface ElementProvider<T extends Affine<T>> {
    @NotNull Affine<T> create(@NotNull PartialModel model);

    @NotNull Affine<T> @NotNull [] create(@NotNull PartialModel model, int count);

    @ApiStatus.NonExtendable
    default void create(@NotNull PartialModel model, Affine<?>[]... out) {
        // the last element will contain the total size
        int[] starts = new int[out.length + 1];
        for (int i = 1; i <= out.length; i++) {
            starts[i] = starts[i - 1] + out[i - 1].length;
        }

        Affine<T>[] all = create(model, starts[out.length]);
        for (int i = 0; i < out.length; i++) {
            System.arraycopy(all, starts[i], out[i], 0, out[i].length);
        }
    }

    @NotNull Pair<Affine<T>, ScrollHandle> createScrolling(@NotNull PartialModel model, @NotNull SpriteShiftEntry shift);

    void freeze();
}
