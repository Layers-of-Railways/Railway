/*
 * Copyright 2022 QuiltMC
 * Modified by the Steam 'n' Rails (Railways) team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.railwayteam.railways.base.datafixerapi;

import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import com.railwayteam.railways.Railways;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.datafix.DataFixers;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.function.BiFunction;

import static com.google.common.base.Preconditions.checkArgument;

@ApiStatus.Internal
public abstract class DataFixesInternals {

    public static BiFunction<Integer, Schema, Schema> baseSchema(BiFunction<Integer, Schema, Schema> factory) {
        return (version, parent) -> {
            checkArgument(version == 0, "version must be 0");
            checkArgument(parent == null, "parent must be null");
            return get().createBaseSchema(factory);
        };
    }

    public record DataFixerEntry(DataFixer dataFixer, int currentVersion) {}

    @Contract(pure = true)
    @Range(from = 0, to = Integer.MAX_VALUE)
    public static int getModDataVersion(@NotNull CompoundTag compound) {
        return compound.getInt("Railways_DataVersion");
    }

    @Contract(pure = true)
    @Range(from = 0, to = Integer.MAX_VALUE)
    public static <T> int getModDataVersion(@NotNull Dynamic<T> dynamic) {
        return dynamic.get("Railways_DataVersion").asInt(0);
    }

    private static DataFixesInternals instance;

    public static @NotNull DataFixesInternals get() {
        if (instance == null) {
            Schema latestVanillaSchema;
            try {
                latestVanillaSchema = DataFixers.getDataFixer()
                    .getSchema(DataFixUtils.makeKey(SharedConstants.getCurrentVersion().getDataVersion().getVersion()));
            } catch (Exception e) {
                latestVanillaSchema = null;
            }

            if (latestVanillaSchema == null) {
                Railways.LOGGER.warn("[Railways DFU] Failed to initialize! Either someone stopped DFU from initializing,");
                Railways.LOGGER.warn("[Railways DFU] or this Minecraft build is hosed.");
                Railways.LOGGER.warn("[Railways DFU] Using no-op implementation.");
                instance = new NoOpDataFixesInternals();
            } else {
                instance = new DataFixesInternalsImpl(latestVanillaSchema);
            }
        }

        return instance;
    }

    public abstract void registerFixer(@Range(from = 0, to = Integer.MAX_VALUE) int currentVersion,
                                       @NotNull DataFixer dataFixer);

    public abstract @Nullable DataFixerEntry getFixerEntry();

    @Contract(value = "_ -> new", pure = true)
    public abstract @NotNull Schema createBaseSchema(@NotNull  BiFunction<Integer, Schema, Schema> factory);

    public abstract <T> @NotNull Dynamic<T> updateWithAllFixers(@NotNull DataFixTypes dataFixTypes, @NotNull Dynamic<T> dynamic);

    public abstract <T> @NotNull Dynamic<T> updateWithAllFixers(@NotNull TypeReference rootType, @NotNull Dynamic<T> dynamic);

    public abstract void addModDataVersions(@NotNull CompoundTag compound);
}
