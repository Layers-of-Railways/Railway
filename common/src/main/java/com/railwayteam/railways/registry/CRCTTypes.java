/*
 * Steam 'n' Rails
 * Copyright (c) 2025 The Railways Team
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

package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import com.simibubi.create.foundation.block.connected.CTType;
import com.simibubi.create.foundation.block.connected.CTTypeRegistry;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour.ContextRequirement;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.resources.ResourceLocation;

public enum CRCTTypes implements CTType {
    VERTICAL_PINKMACHINE(2, ContextRequirement.builder().vertical().build()) {
        @Override
        public int getTextureIndex(ConnectedTextureBehaviour.CTContext context) {
            return !context.up && !context.down
                ? 0 // single
                : !context.up
                ? 3 // top
                : !context.down
                ? 2 // bottom
                : 1; // middle
        }
    }
    ;

    private final ResourceLocation id;
    private final int sheetSize;
    private final ContextRequirement contextRequirement;

    CRCTTypes(int sheetSize, ContextRequirement contextRequirement) {
        this.id = Railways.asResource(Lang.asId(name()));
        this.sheetSize = sheetSize;
        this.contextRequirement = contextRequirement;

        CTTypeRegistry.register(this);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public int getSheetSize() {
        return sheetSize;
    }

    @Override
    public ContextRequirement getContextRequirement() {
        return contextRequirement;
    }
}
