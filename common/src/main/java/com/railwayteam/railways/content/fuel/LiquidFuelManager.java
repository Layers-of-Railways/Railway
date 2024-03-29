package com.railwayteam.railways.content.fuel;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class LiquidFuelManager {
    private static final Map<ResourceLocation, LiquidFuelType> CUSTOM_TYPE_MAP = new HashMap<>();
    private static final Map<Fluid, LiquidFuelType> FLUID_TO_TYPE_MAP = new IdentityHashMap<>();

    public static void clear() {
        CUSTOM_TYPE_MAP.clear();
        FLUID_TO_TYPE_MAP.clear();
    }

    public static LiquidFuelType getTypeForFluid(Fluid fluid) {
        return FLUID_TO_TYPE_MAP.get(fluid);
    }

    public static void fillFluidMap() {
        for (Map.Entry<ResourceLocation, LiquidFuelType> entry : CUSTOM_TYPE_MAP.entrySet()) {
            LiquidFuelType type = entry.getValue();
            for (Supplier<Fluid> delegate : type.getFluids()) {
                FLUID_TO_TYPE_MAP.put(delegate.get(), type);
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
                    CUSTOM_TYPE_MAP.put(id, type);
                }
            }

            fillFluidMap();
        }
    }
}
