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
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

/**
 * Represents a special ingredient for datagen - it references an item that does not necessarily exist
 */
public class SoftIngredient extends Ingredient {
    public final ResourceLocation item;
    public SoftIngredient(ResourceLocation item) {
        super(Stream.empty());
        this.item = item;
    }

    public static SoftIngredient of(ResourceLocation item) {
        return new SoftIngredient(item);
    }

    @Override
    public @NotNull JsonElement toJson() {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("item", item.toString());
        return jsonobject;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
