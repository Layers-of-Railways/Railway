/*
 * Steam 'n' Rails
 * Copyright (c) 2026 The Railways Team
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

package com.railwayteam.railways.base.data.fabric;

import com.railwayteam.railways.registry.CRPotatoProjectileTypes;
import com.simibubi.create.api.registry.CreateRegistries;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class GeneratedEntriesProvider extends FabricDynamicRegistryProvider {
    public GeneratedEntriesProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {
        entries.addAll(registries.lookupOrThrow(CreateRegistries.POTATO_PROJECTILE_TYPE));
    }

    public static RegistrySetBuilder addBootstraps(RegistrySetBuilder builder) {
        return builder.add(CreateRegistries.POTATO_PROJECTILE_TYPE, CRPotatoProjectileTypes::bootstrap);
    }

    @Override
    public @NotNull String getName() {
        return "Railways' Generated Registry Entries";
    }
}
