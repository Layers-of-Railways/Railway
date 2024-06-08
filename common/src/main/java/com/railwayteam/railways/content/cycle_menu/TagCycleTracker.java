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

package com.railwayteam.railways.content.cycle_menu;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagCycleTracker {
    private final List<TagKey<Item>> CYCLING_TAGS = new ArrayList<>();
    private final Map<TagKey<Item>, List<Item>> CYCLES = new HashMap<>();
    private final Map<Item, TagKey<Item>> REVERSE_LOOKUP = new HashMap<>();
    private boolean cyclesComputed = false;

    public void registerCycle(TagKey<Item> tag) {
        if (!CYCLING_TAGS.contains(tag))
            CYCLING_TAGS.add(tag);
    }

    public void scheduleRecompute() {
        cyclesComputed = false;
    }

    public void computeCycles() {
        CYCLES.clear();
        REVERSE_LOOKUP.clear();
        CYCLING_TAGS.forEach(tag -> CYCLES.put(tag, new ArrayList<>()));

        BuiltInRegistries.ITEM.holders().forEachOrdered(item -> {
            for (TagKey<Item> tag : CYCLING_TAGS) {
                if (item.is(tag)) {
                    CYCLES.get(tag).add(item.value());
                    REVERSE_LOOKUP.put(item.value(), tag);
                }
            }
        });
        cyclesComputed = true;
    }

    public @Nullable TagKey<Item> getCycleTag(Item item) {
        if (!cyclesComputed) computeCycles();
        return REVERSE_LOOKUP.get(item);
    }

    public List<Item> getCycle(TagKey<Item> tag) {
        if (!cyclesComputed) computeCycles();
        return CYCLES.getOrDefault(tag, ImmutableList.of());
    }
}
