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

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.railwayteam.railways.registry.CRPalettes;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

public class EmiRecipeDefaultsGen implements DataProvider {
    public static final List<ResourceLocation> DEFAULT_RECIPES = new ArrayList<>();
    public static final Map<TagKey<Item>, ResourceLocation> TAG_DEFAULTS = new LinkedHashMap<>(); // preserve insertion order

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final DataGenerator gen;

    public EmiRecipeDefaultsGen(DataGenerator gen) {
        this.gen = gen;
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void run(@NotNull CachedOutput output) throws IOException {
        Path path = this.gen.getOutputFolder()
            .resolve("assets/emi/recipe/defaults/railways.json");

        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        HashingOutputStream hashingoutputstream = new HashingOutputStream(Hashing.sha1(), bytearrayoutputstream);

        Writer writer = new OutputStreamWriter(hashingoutputstream, StandardCharsets.UTF_8);
        writer.append(run());
        writer.close();

        output.writeIfNeeded(path, bytearrayoutputstream.toByteArray(), hashingoutputstream.hash());
    }

    private String run() {
        JsonObject object = new JsonObject();

        JsonArray added = new JsonArray();
        JsonObject tags = new JsonObject();

        DEFAULT_RECIPES.forEach(loc -> added.add(loc.toString()));
        TAG_DEFAULTS.put(CRPalettes.CYCLE_GROUPS.get(null), CRPalettes.Styles.RIVETED.get(null).getId());
        TAG_DEFAULTS.forEach((tag, itemLoc) -> {
            String tagString = "#item:" + tag.location();
            String itemString = "item:" + itemLoc;
            tags.addProperty(tagString, itemString);
        });

        object.add("added", added);
        object.add("tags", tags);
        object.add("resolutions", new JsonObject());
        object.add("disabled", new JsonArray());

        return GSON.toJson(object);
    }

    @Override
    public @NotNull String getName() {
        return "Steam 'n' Rails EMI recipe tree defaults";
    }
}
