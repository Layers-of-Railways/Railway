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

package com.railwayteam.railways.content.bogey_menu.handler;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.api.bogeymenu.v0.entry.BogeyEntry;
import com.railwayteam.railways.registry.CRBogeyStyles;
import com.railwayteam.railways.registry.CRTrackMaterials;
import com.simibubi.create.AllBogeyStyles;
import com.simibubi.create.content.trains.bogey.BogeySizes.BogeySize;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.simibubi.create.foundation.gui.widget.Indicator;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BogeyMenuHandlerClient {
    private static final Map<BogeyStyle, List<Pair<BogeyStyle, BogeySize>>> CACHED_RENDER_CYCLES = new HashMap<>();
    private static final Map<BogeyEntry, Indicator.State[]> CACHED_COMPATS = new HashMap<>();

    @Nullable
    private static List<BogeyStyle> favorites = null;

    public static void addFavorite(BogeyStyle style) {
        if (favorites == null)
            loadFavorites();
        if (favorites.contains(style))
            return;
        favorites.add(style);
        saveFavorites();
    }

    public static void removeFavorite(BogeyStyle style) {
        if (favorites == null)
            loadFavorites();
        if (!favorites.contains(style))
            return;
        favorites.remove(style);
        saveFavorites();
    }

    public static void toggleFavorite(BogeyStyle style) {
        if (favorites == null)
            loadFavorites();
        if (favorites.contains(style))
            removeFavorite(style);
        else
            addFavorite(style);
    }

    public static boolean isFavorited(BogeyStyle style) {
        if (favorites == null)
            loadFavorites();
        return favorites.contains(style);
    }

    /**
     * Prefer using {@link #addFavorite} and {@link #removeFavorite} for those operations
     * @return a list of favorited bogey styles
     */
    @NotNull
    public static List<BogeyStyle> getFavorites() {
        if (favorites == null)
            loadFavorites();
        return favorites;
    }

    private static void optimizeFavorites() {
        List<BogeyStyle> newFavorites = new ArrayList<>();
        for (BogeyStyle style : getFavorites()) {
            if (!newFavorites.contains(style))
                newFavorites.add(style);
        }
        favorites = newFavorites;
        saveFavorites();
    }

    private static void loadFavorites() {
        favorites = new ArrayList<>();
        try {
            Minecraft mc = Minecraft.getInstance();
            File file = new File(mc.gameDirectory, "snr_favorite_styles.nbt");
            CompoundTag tag = NbtIo.read(file);
            if (tag == null)
                return;

            if (tag.contains("Favorites", Tag.TAG_LIST)) {
                ListTag favoritesList = tag.getList("Favorites", Tag.TAG_STRING);
                if (favorites == null)
                    favorites = new ArrayList<>();
                favorites.clear();
                for (Tag favoriteTag : favoritesList) {
                    if (favoriteTag instanceof StringTag stringTag) {
                        ResourceLocation loc = ResourceLocation.tryParse(stringTag.getAsString());
                        if (loc == null)
                            continue;
                        if (AllBogeyStyles.BOGEY_STYLES.containsKey(loc)) {
                            favorites.add(AllBogeyStyles.BOGEY_STYLES.get(loc));
                        }
                    }
                }
            }
            optimizeFavorites();
        } catch (Exception e) {
            Railways.LOGGER.error("Failed to load favorite styles", e);
        }
    }

    private static void saveFavorites() {
        if (favorites == null)
            return;
        try {
            CompoundTag tag = new CompoundTag();
            ListTag listTag = new ListTag();
            for (BogeyStyle style : favorites) {
                listTag.add(StringTag.valueOf(style.name.toString()));
            }
            tag.put("Favorites", listTag);
            NbtIo.write(tag, new File(Minecraft.getInstance().gameDirectory, "snr_favorite_styles.nbt"));
        } catch (Exception e) {
            Railways.LOGGER.error("Failed to save favorite styles", e);
        }
    }

    public static @Nullable BogeySize getSize(BogeyStyle style) {
        for (BogeySize size : style.validSizes()) {
            return size;
        }
        return null;
    }

    public static List<Pair<BogeyStyle, BogeySize>> getRenderCycle(BogeyStyle style) {
         return CACHED_RENDER_CYCLES.computeIfAbsent(style, (s) -> {
            List<Pair<BogeyStyle, BogeySize>> cycle = new ArrayList<>();
            for (BogeySize size : style.validSizes()) {
                cycle.add(Pair.of(style, size));
            }

            for (BogeyStyle subStyle : CRBogeyStyles.getSubStyles(style)) {
                for (BogeySize size : subStyle.validSizes()) {
                    cycle.add(Pair.of(subStyle, size));
                }
            }

            return cycle;
        });
    }

    public static Indicator.State[] getTrackCompat(BogeyEntry bogeyEntry) {
        return CACHED_COMPATS.computeIfAbsent(bogeyEntry, (k) -> new Indicator.State[] {
                styleFits(bogeyEntry, CRTrackMaterials.CRTrackType.NARROW_GAUGE),
                styleFits(bogeyEntry, TrackMaterial.TrackType.STANDARD),
                styleFits(bogeyEntry, CRTrackMaterials.CRTrackType.WIDE_GAUGE)
        });
    }

    private static Indicator.State styleFits(BogeyEntry bogeyEntry, TrackMaterial.TrackType trackType) {
        if (CRBogeyStyles.styleFitsTrack(bogeyEntry.bogeyStyle(), trackType))
            return Indicator.State.GREEN;

        for (BogeyStyle subStyle : CRBogeyStyles.getSubStyles(bogeyEntry.bogeyStyle()))
            if (CRBogeyStyles.styleFitsTrack(subStyle, trackType))
                return Indicator.State.GREEN;

        return Indicator.State.RED;
    }
}
