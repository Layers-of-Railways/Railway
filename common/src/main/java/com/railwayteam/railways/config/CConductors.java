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
public class CConductors extends ConfigBase {

    public final ConfigBool whistleRequiresOwning = b(false, "mustOwnBoundTrain", Comments.whistleRequiresOwning);
    public final ConfigInt maxVentLength = i(64, 1, Integer.MAX_VALUE, "maxConductorVentLength", Comments.maxVentLength);
    public final ConfigInt whistleRebindRate = i(10, 1, 600, "whistleRebindRate", Comments.whistleRebindRate);

    @Override
    public String getName() {
        return "conductors";
    }

    private static class Comments {
        static String whistleRequiresOwning = "Conductor whistle is limited to the owner of a train";
        static String maxVentLength = "Maximum length of conductor vents";
        static String whistleRebindRate = "How often a conductor whistle updates the train of the bound conductor";
    }
}
