package com.railwayteam.railways.content.fuel;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class LiquidFuelType {
    private final List<Supplier<Fluid>> fluids = new ArrayList<>();

    private int fuelTicks = 40;

    public LiquidFuelType() {}

    public List<Supplier<Fluid>> getFluids() {
        return fluids;
    }

    public int getFuelTicks() {
        return fuelTicks;
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
                                Fluid fluid = BuiltInRegistries.FLUID.get(new ResourceLocation(primitive.getAsString()));
                                if (fluid != null) {
                                    type.fluids.add(() -> fluid);
                                }
                            } catch (ResourceLocationException ignored) {}
                        }
                    }
                }
            }

            parseJsonPrimitive(object, "fuel_ticks", JsonPrimitive::isNumber, primitive -> type.fuelTicks = primitive.getAsInt());
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
