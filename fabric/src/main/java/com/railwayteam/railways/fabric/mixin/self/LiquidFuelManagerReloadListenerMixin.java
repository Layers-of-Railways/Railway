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

package com.railwayteam.railways.fabric.mixin.self;

import com.google.gson.Gson;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.fuel.LiquidFuelManager;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Self mixin, I know funny right, by the power of casting I cast you to work.
 */
@Mixin(LiquidFuelManager.ReloadListener.class)
public abstract class LiquidFuelManagerReloadListenerMixin extends SimpleJsonResourceReloadListener implements IdentifiableResourceReloadListener {
    public LiquidFuelManagerReloadListenerMixin(Gson gson, String directory) {
        super(gson, directory);
    }

    @Override
    public ResourceLocation getFabricId() {
        return Railways.asResource(LiquidFuelManager.ReloadListener.ID);
    }
}
