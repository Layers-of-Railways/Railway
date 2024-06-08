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
public class CJourneymap extends ConfigBase {

    public final ConfigInt farTrainSyncTicks = i(200, 10, 600, "farTrainSyncTicks", Comments.inTicks, Comments.farTrainSyncTicks);
    public final ConfigInt nearTrainSyncTicks = i(5, 1, 600, "nearTrainSyncTicks", Comments.inTicks, Comments.nearTrainSyncTicks);

    @Override
    public String getName() {
        return "journeymap";
    }

    private static class Comments {
        static String inTicks = "[in Ticks]";

        static String farTrainSyncTicks = "Outside-of-render-distance train sync time";
        static String nearTrainSyncTicks = "In-render-distance train sync time";
    }
}
