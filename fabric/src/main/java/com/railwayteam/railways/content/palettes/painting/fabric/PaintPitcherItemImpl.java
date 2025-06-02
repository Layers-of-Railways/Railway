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

package com.railwayteam.railways.content.palettes.painting.fabric;

import com.railwayteam.railways.annotation.multiloader.ImplClass;
import com.railwayteam.railways.content.palettes.PalettesColor;
import com.railwayteam.railways.content.palettes.painting.PaintPitcherItem;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.Item;

import javax.annotation.ParametersAreNonnullByDefault;

@ImplClass
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@SuppressWarnings("UnstableApiUsage")
public class PaintPitcherItemImpl extends PaintPitcherItem {
    public PaintPitcherItemImpl(Properties properties, PalettesColor color) {
        super(properties, color);

        FluidStorage.ITEM.registerForItems(($, context) -> new PaintPitcherFluidStorage(context), this);
    }

    public static PaintPitcherItem create(Item.Properties properties, PalettesColor color) {
        return new PaintPitcherItemImpl(properties, color);
    }
}
