/*
 * Steam 'n' Rails
 * Copyright (c) 2024 The Railways Team
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

package com.railwayteam.railways.fabric.mixin.self;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.custom_tracks.casing.CasingResourceReloadListener;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(CasingResourceReloadListener.class)
public abstract class CasingResourceReloadListenerMixin implements ResourceManagerReloadListener, IdentifiableResourceReloadListener {
    @Unique
    private static final ResourceLocation ID = Railways.asResource("casing_reload_listener");
    
    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }
}
