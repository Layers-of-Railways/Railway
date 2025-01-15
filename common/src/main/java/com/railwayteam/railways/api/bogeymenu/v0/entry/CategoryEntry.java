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

package com.railwayteam.railways.api.bogeymenu.v0.entry;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.bogey_menu.handler.BogeyMenuHandlerClient;
import com.railwayteam.railways.impl.bogeymenu.v0.BogeyMenuManagerImpl;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CategoryEntry {
    private final @NotNull Component name;
    private final @NotNull ResourceLocation id;
    private final @NotNull List<BogeyEntry> bogeyEntryList = new ArrayList<>();

    public CategoryEntry(@NotNull Component name, @NotNull ResourceLocation id) {
        this.name = name;
        this.id = id;
    }

    public @NotNull Component getName() {
        return name;
    }

    public @NotNull ResourceLocation getId() {
        return id;
    }

    public @NotNull List<BogeyEntry> getBogeyEntryList() {
        return bogeyEntryList;
    }

    @ApiStatus.Internal
    void addToBogeyEntryList(BogeyEntry entry) {
        bogeyEntryList.add(entry);
    }

    public static class FavoritesCategory extends CategoryEntry {
        public static final CategoryEntry INSTANCE = new FavoritesCategory();

        @Nullable
        private List<BogeyEntry> cachedEntryList = null;
        private int cachedVersion = -1;

        private FavoritesCategory() {
            super(Component.translatable("railways.gui.bogey_menu.category.favorites"), Railways.asResource("favorites"));
        }

        @Override
        public @NotNull List<BogeyEntry> getBogeyEntryList() {
            int version = BogeyMenuHandlerClient.getFavorites().hashCode();
            if (cachedEntryList == null || cachedVersion != version) {
                cachedEntryList = new ArrayList<>();
                for (BogeyStyle style : BogeyMenuHandlerClient.getFavorites()) {
                    if (BogeyEntry.STYLE_TO_ENTRY.containsKey(style)) {
                        cachedEntryList.add(BogeyEntry.STYLE_TO_ENTRY.get(style));
                    } else {
                        // uh oh, we're going to have to guess values here...
                        cachedEntryList.add(BogeyEntry.getOrCreate(style, null, BogeyMenuManagerImpl.defaultScale));
                    }
                }
                cachedVersion = version;
            }
            return cachedEntryList;
        }

        @Override
        void addToBogeyEntryList(BogeyEntry entry) {
            // no-op
        }
    }
}
