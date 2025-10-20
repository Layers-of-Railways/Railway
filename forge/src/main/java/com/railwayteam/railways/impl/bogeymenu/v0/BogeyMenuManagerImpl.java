/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2025 The Railways Team
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

package com.railwayteam.railways.impl.bogeymenu.v0;

import com.railwayteam.railways.api.bogeymenu.v0.BogeyMenuManager;
import com.railwayteam.railways.api.bogeymenu.v0.entry.BogeyEntry;
import com.railwayteam.railways.api.bogeymenu.v0.entry.CategoryEntry;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.bogey.BogeySizes.BogeySize;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import net.createmod.catnip.data.Pair;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApiStatus.Internal
public class BogeyMenuManagerImpl implements BogeyMenuManager {
     /** Internal use only, do NOT touch. */
    @ApiStatus.Internal public static final List<CategoryEntry> CATEGORIES = new ArrayList<>();
    /** Internal use only, do NOT touch. */
    @ApiStatus.Internal public static final Map<Pair<BogeyStyle, BogeySize>, Float> SIZES_TO_SCALE = new ConcurrentHashMap<>();

    static {
        CATEGORIES.add(CategoryEntry.FavoritesCategory.INSTANCE);
    }

    public static final float defaultScale = 23;

    @Override
    public CategoryEntry registerCategory(@NotNull Component name, @NotNull ResourceLocation id) {
        CategoryEntry entry = new CategoryEntry(name, id);
        // maintain favorites category at the end
        CATEGORIES.add(CATEGORIES.size() - 1, entry);
        return entry;
    }

    @Override
    public @Nullable CategoryEntry getCategoryById(@NotNull ResourceLocation id) {
        for (CategoryEntry categoryEntry : CATEGORIES) {
            if (categoryEntry.getId().equals(id))
                return categoryEntry;
        }
        return null;
    }

    @Override
    public BogeyEntry addToCategory(@NotNull CategoryEntry categoryEntry, @NotNull BogeyStyle bogeyStyle, @Nullable ResourceLocation iconLocation) {
        return addToCategory(categoryEntry, bogeyStyle, iconLocation, defaultScale);
    }

    @Override
    public BogeyEntry addToCategory(@NotNull CategoryEntry categoryEntry, @NotNull BogeyStyle bogeyStyle, @Nullable ResourceLocation iconLocation, float scale) {
        return BogeyEntry.getOrCreate(categoryEntry, bogeyStyle, iconLocation, scale);
    }

    @Override
    public void setScalesForBogeySizes(BogeyStyle style, BogeySizes.BogeySize size, float scale) {
        SIZES_TO_SCALE.put(Pair.of(style, size), scale);
    }
}
