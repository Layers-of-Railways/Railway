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

package com.railwayteam.railways.registry.fabric;

import com.railwayteam.railways.registry.CRExtraRegistration;
import com.simibubi.create.Create;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class CRExtraRegistrationImpl {
    private static final ResourceLocation COPYCAT_ID = Create.asResource("copycat");

    public static void platformSpecificRegistration() {
        RegistryEntryAddedCallback.event(Registry.BLOCK_ENTITY_TYPE).register((rawId, id, blockEntityType) ->  {
            if (id == COPYCAT_ID)
                CRExtraRegistration.addVentAsCopycat(blockEntityType);
        });
    }
}
