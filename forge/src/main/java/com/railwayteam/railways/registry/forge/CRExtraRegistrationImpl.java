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

import com.railwayteam.railways.registry.CRExtraRegistration;
import com.simibubi.create.Create;
import com.simibubi.create.api.registrate.CreateRegistrateRegistrationCallback;
import net.minecraft.core.registries.Registries;

public class CRExtraRegistrationImpl {
    public static void platformSpecificRegistration() {
        CreateRegistrateRegistrationCallback.register(Registries.BLOCK_ENTITY_TYPE, Create.asResource("copycat"), CRExtraRegistration::addVentAsCopycat);
        CreateRegistrateRegistrationCallback.register(Registries.BLOCK, Create.asResource("track_signal"), CRExtraRegistration::addSignalSource);
    }
}
