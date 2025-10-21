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

package com.railwayteam.railways.compat.tracks;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a special ingredient for datagen - it references an item that does not necessarily exist.
 * In Minecraft 1.21+, Ingredient became final so this class no longer extends it.
 * It provides a similar interface for use in track material registration.
 */
public class SoftIngredient {
    public final ResourceLocation item;
    
    private SoftIngredient(ResourceLocation item) {
        this.item = item;
    }

    public static SoftIngredient of(ResourceLocation item) {
        return new SoftIngredient(item);
    }

    public @NotNull JsonElement toJson() {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("item", item.toString());
        return jsonobject;
    }

    public boolean isEmpty() {
        return false;
    }
}
