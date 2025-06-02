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

package com.railwayteam.railways.content.palettes.painting.forge;

import com.railwayteam.railways.annotation.multiloader.ImplClass;
import com.railwayteam.railways.content.palettes.painting.EmptyPaintPitcherItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;

@ImplClass
public class EmptyPaintPitcherItemImpl extends EmptyPaintPitcherItem {
    public EmptyPaintPitcherItemImpl(Properties properties) {
        super(properties);
    }

    public static EmptyPaintPitcherItem create(Item.Properties properties) {
        return new EmptyPaintPitcherItemImpl(properties);
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new PaintPitcherCapability(stack);
    }
}
