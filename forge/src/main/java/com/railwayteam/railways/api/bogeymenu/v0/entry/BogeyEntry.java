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

package com.railwayteam.railways.api.bogeymenu.v0.entry;

import com.simibubi.create.content.trains.bogey.BogeyStyle;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public record BogeyEntry(@NotNull BogeyStyle bogeyStyle, @Nullable ResourceLocation iconLocation, float scale) {
    public static final Map<BogeyStyle, BogeyEntry> STYLE_TO_ENTRY = new ConcurrentHashMap<>();

    public static BogeyEntry getOrCreate(@NotNull CategoryEntry categoryEntry, @NotNull BogeyStyle bogeyStyle, @Nullable ResourceLocation iconLocation, float scale) {
        BogeyEntry entry = getOrCreate(bogeyStyle, iconLocation, scale);
        categoryEntry.addToBogeyEntryList(entry);
        return entry;
    }

    public static BogeyEntry getOrCreate(@NotNull BogeyStyle bogeyStyle, @Nullable ResourceLocation iconLocation, float scale) {
        return STYLE_TO_ENTRY.computeIfAbsent(bogeyStyle, (bs) -> new BogeyEntry(bogeyStyle, iconLocation, scale));
    }
}
