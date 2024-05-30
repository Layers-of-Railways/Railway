/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.content.fuel;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class LiquidFuelType {
    private final List<Supplier<Fluid>> fluids = new ArrayList<>();
    private final List<Supplier<TagKey<Fluid>>> fluidTags = new ArrayList<>();

    private int fuelTicks = 400;
    private boolean invalid = false;

    public LiquidFuelType() { }

    public List<Supplier<Fluid>> getFluids() {
        return fluids;
    }

    public List<Supplier<TagKey<Fluid>>> getFluidTags() {
        return fluidTags;
    }

    public int getFuelTicks() {
        return fuelTicks;
    }

    public boolean getInvalid() {
        return invalid;
    }

    public static LiquidFuelType fromJson(JsonObject object) {
        LiquidFuelType type = new LiquidFuelType();
        try {
            JsonElement fluidsElement = object.get("fluids");
            if (fluidsElement != null && fluidsElement.isJsonArray()) {
                for (JsonElement element : fluidsElement.getAsJsonArray()) {
                    if (element.isJsonPrimitive()) {
                        JsonPrimitive primitive = element.getAsJsonPrimitive();
                        if (primitive.isString()) {
                            try {
                                String string = primitive.getAsString();

                                if (string.startsWith("#")) {
                                    TagKey<Fluid> tag = TagKey.create(Registries.FLUID, new ResourceLocation(primitive.getAsString().substring(1)));
                                    if (tag != null) {
                                        type.fluidTags.add(() -> tag);
                                    }
                                } else {
                                    Fluid fluid = BuiltInRegistries.FLUID.get(new ResourceLocation(primitive.getAsString()));
                                    if (fluid != null) {
                                        type.fluids.add(() -> fluid);
                                    }
                                }
                            } catch (ResourceLocationException ignored) {}
                        }
                    }
                }
            } else {
                return null;
            }

            parseJsonPrimitive(object, "fuel_ticks", JsonPrimitive::isNumber, primitive -> type.fuelTicks = primitive.getAsInt());
            parseJsonPrimitive(object, "invalid", JsonPrimitive::isBoolean, primitive -> type.invalid = primitive.getAsBoolean());
        } catch (Exception ignored) {}

        return type;
    }

    private static void parseJsonPrimitive(JsonObject object, String key, Predicate<JsonPrimitive> predicate, Consumer<JsonPrimitive> consumer) {
        JsonElement element = object.get(key);
        if (element != null && element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (predicate.test(primitive)) {
                consumer.accept(primitive);
            }
        }
    }
}
