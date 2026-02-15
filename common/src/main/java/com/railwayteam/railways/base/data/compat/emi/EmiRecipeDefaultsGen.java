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

package com.railwayteam.railways.base.data.compat.emi;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.palettes.PalettesColor;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class EmiRecipeDefaultsGen implements DataProvider {
    public static final List<ResourceLocation> DEFAULT_RECIPES = new ArrayList<>();
    public static final Map<TagKey<Item>, ResourceLocation> TAG_DEFAULTS = new LinkedHashMap<>(); // preserve insertion order

    private final PackOutput packOutput;

    public EmiRecipeDefaultsGen(PackOutput packOutput) {
        this.packOutput = packOutput;
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput output) {
        Path path = this.packOutput.getOutputFolder()
            .resolve("assets/emi/recipe/defaults/railways.json");

        addMisc();

        return DataProvider.saveStable(output, run(), path);
    }

    private void addMisc() {
        // (forge) JEI compat paint pitcher filling recipes
        for (PalettesColor color : PalettesColor.values()) {
            if (color.isNetherite()) continue;
            String path = "create/filling/railways/empty_paint_pitcher/with/railways/paint/" + color.getSerializedName();
            DEFAULT_RECIPES.add(new ResourceLocation("emi", path));
            DEFAULT_RECIPES.add(jeiMangle(Railways.asResource(path)));
        }
    }

    private JsonElement run() {
        JsonObject object = new JsonObject();

        JsonArray added = new JsonArray();
        JsonObject tags = new JsonObject();

        DEFAULT_RECIPES.forEach(loc -> added.add(loc.toString()));
        for (ResourceLocation loc : DEFAULT_RECIPES) {
            if (loc.getNamespace().equals("jei")) continue;
            if (loc.getNamespace().equals("emi")) continue;
            added.add(jeiMangle(loc).toString());
        }

        TAG_DEFAULTS.forEach((tag, itemLoc) -> {
            String tagString = "#item:" + tag.location();
            String itemString = "item:" + itemLoc;
            tags.addProperty(tagString, itemString);
        });

        object.add("added", added);
        object.add("tags", tags);
        object.add("resolutions", new JsonObject());
        object.add("disabled", new JsonArray());

        return object;
    }

    private static ResourceLocation jeiMangle(ResourceLocation loc) {
        return new ResourceLocation("jei", "/" + loc.getNamespace() + "/" + loc.getPath());
    }

    @Override
    public @NotNull String getName() {
        return "Steam 'n' Rails EMI recipe tree defaults";
    }
}
