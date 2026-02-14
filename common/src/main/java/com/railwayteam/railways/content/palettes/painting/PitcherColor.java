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

package com.railwayteam.railways.content.palettes.painting;

import com.railwayteam.railways.content.palettes.PalettesColor;
import com.railwayteam.railways.registry.CRItems;
import com.tterrag.registrate.util.entry.ItemEntry;
import org.jetbrains.annotations.Nullable;

public record PitcherColor(@Nullable PalettesColor color) {
    public static final PitcherColor SANDY_WATER = new PitcherColor(null);

    public boolean isSandyWater() {
        return this.color == null;
    }

    public ItemEntry<? extends PaintPitcherItem> getItemEntry() {
        return isSandyWater() ? CRItems.SANDY_PITCHER : CRItems.PAINT_PITCHERS.get(color);
    }
}
