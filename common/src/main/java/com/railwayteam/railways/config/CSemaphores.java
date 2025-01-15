/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2025 The Railways Team
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

package com.railwayteam.railways.config;

import net.createmod.catnip.config.ConfigBase;

@SuppressWarnings("unused")
public class CSemaphores extends ConfigBase {

    public final ConfigBool simplifiedPlacement = b(true, "simplifiedPlacement", Comments.simplifiedPlacement);
    public final ConfigBool flipYellowOrder = b(false, "flipYellowOrder", Comments.flipYellowOrder);

    @Override
    public String getName() {
        return "semaphores";
    }

    private static class Comments {
        static String simplifiedPlacement = "Simplified semaphore placement (no upside-down placement)";
        static String flipYellowOrder = "Whether semaphore color order is reversed when the semaphores are oriented upside-down";
    }
}
