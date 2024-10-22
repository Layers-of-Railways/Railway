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

package com.railwayteam.railways.config;

import com.simibubi.create.foundation.config.ConfigBase;

@SuppressWarnings("unused")
public class CRealism extends ConfigBase {
    public final ConfigBool realisticTrains = b(false, "realisticTrains", Comments.realisticTrains);
    public final ConfigBool realisticFuelTanks = b(true, "realisticFuelTanks", Comments.realisticFuelTanks);

    public final ConfigInt netherExtraFuelUsage = i(1, 1, Integer.MAX_VALUE, Comments.netherExtraFuelUsage);
    
    @Override
    public String getName() {
            return "realism";
    }

    private static class Comments {
        static String realisticTrains = "Make trains require fuel to run (either from fuel tanks or solid fuels in chests/barrels)";
        static String realisticFuelTanks = "Make fuel tanks only accept proper liquid fuels (so water etc can't go into them)";
        
        static String netherExtraFuelUsage = "The number of extra fuel ticks trains in the nether will use, 1 means they will not use extra fuel, 8 means they'll use roughly the same amount as if they were travelling to the same place via the Overworld";
    }
}
