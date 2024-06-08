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

package com.railwayteam.railways.content.fuel;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class LiquidFuelManager {
    private static final Map<ResourceLocation, LiquidFuelType> CUSTOM_TYPE_MAP = new HashMap<>();
    private static final Map<Fluid, LiquidFuelType> FLUID_TO_TYPE_MAP = new IdentityHashMap<>();
    private static final Map<TagKey<Fluid>, LiquidFuelType> TAG_TO_TYPE_MAP = new IdentityHashMap<>();

    public static void clear() {
        CUSTOM_TYPE_MAP.clear();
        FLUID_TO_TYPE_MAP.clear();
        TAG_TO_TYPE_MAP.clear();
    }

    public static LiquidFuelType getTypeForFluid(Fluid fluid) {
        return FLUID_TO_TYPE_MAP.get(fluid);
    }

    @Nullable
    public static LiquidFuelType isInTag(Fluid fluid) {
        for (Map.Entry<TagKey<Fluid>, LiquidFuelType> entry : TAG_TO_TYPE_MAP.entrySet()) {
            if (fluid.defaultFluidState().is(entry.getKey())) {
                return entry.getValue();
            }
        }

        return null;
    }

    public static void fillFluidMap() {
        for (Map.Entry<ResourceLocation, LiquidFuelType> entry : CUSTOM_TYPE_MAP.entrySet()) {
            LiquidFuelType type = entry.getValue();
            for (Supplier<Fluid> delegate : type.getFluids()) {
                FLUID_TO_TYPE_MAP.put(delegate.get(), type);
            }
            for (Supplier<TagKey<Fluid>> delegate : type.getFluidTags()) {
                TAG_TO_TYPE_MAP.put(delegate.get(), type);
            }
        }
    }

    public static class ReloadListener extends SimpleJsonResourceReloadListener {
        private static final Gson GSON = new Gson();
        public static final ReloadListener INSTANCE = new ReloadListener();
        public static final String ID = "railways_liquid_fuel";

        protected ReloadListener() {
            super(GSON, ID);
        }

        @Override
        protected void apply(@NotNull Map<ResourceLocation, JsonElement> map, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
            clear();

            for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
                JsonElement element = entry.getValue();
                if (element.isJsonObject()) {
                    ResourceLocation id = entry.getKey();
                    JsonObject object = element.getAsJsonObject();
                    LiquidFuelType type = LiquidFuelType.fromJson(object);

                    if (type != null) {
                        CUSTOM_TYPE_MAP.put(id, type);
                    }
                }
            }

            fillFluidMap();
        }
    }
}
