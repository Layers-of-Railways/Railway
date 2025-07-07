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

package com.railwayteam.railways.registry.forge;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.registry.CRExtraRegistration;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraft.core.registries.Registries;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

public class CRExtraRegistrationImpl {
    private static final CreateRegistrate REGISTRATE;
    
    static {
        CreateRegistrate localRegistrate = null;
        
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodHandles.Lookup privateLookup = MethodHandles.privateLookupIn(Create.class, lookup);

            VarHandle handle = privateLookup.findStaticVarHandle(Create.class, "REGISTRATE", CreateRegistrate.class);
            localRegistrate = (CreateRegistrate) handle.get();
        } catch (Exception e) {
            Railways.LOGGER.error("Failed to get Create's Registrate Instance, This should not happen!!", e);
        }
        
        REGISTRATE = localRegistrate;
	}
    
    public static void platformSpecificRegistration() {
        if (REGISTRATE != null) {
            REGISTRATE.addRegisterCallback("copycat", Registries.BLOCK_ENTITY_TYPE, CRExtraRegistration::addVentAsCopycat);
            REGISTRATE.addRegisterCallback("track_signal", Registries.BLOCK, CRExtraRegistration::addSignalSource);
        }
    }
}
