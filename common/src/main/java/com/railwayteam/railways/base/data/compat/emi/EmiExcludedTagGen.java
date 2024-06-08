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
import com.railwayteam.railways.multiloader.CommonTag;
import com.railwayteam.railways.multiloader.CommonTags;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class EmiExcludedTagGen implements DataProvider {
    private static final String INDENT = "  ";
    private final PackOutput packOutput;

    public EmiExcludedTagGen(PackOutput packOutput) {
        this.packOutput = packOutput;
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput output) {
        Path path = this.packOutput.getOutputFolder()
            .resolve("assets/emi/tag/exclusions/railways.json");

        return DataProvider.saveStable(output, run(), path);
    }

    private JsonElement run() {
        JsonObject object = new JsonObject();
        {
            JsonArray item = new JsonArray();
            // fill in items
            for (CommonTag<Item> itemTag : CommonTags.ALL_ITEMS) {
                item.add(itemTag.tag.location().toString());
            }
            object.add("item", item);
        }
        {
            JsonArray block = new JsonArray();
            // fill in blocks
            for (CommonTag<Block> blockTag : CommonTags.ALL_BLOCKS) {
                block.add(blockTag.tag.location().toString());
            }
            object.add("block", block);
        }
        return object;
    }

    @Override
    public String getName() {
        return "Steam 'n' Rails EMI excluded tags";
    }
}
