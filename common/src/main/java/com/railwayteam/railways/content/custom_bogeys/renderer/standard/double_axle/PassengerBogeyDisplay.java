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

package com.railwayteam.railways.content.custom_bogeys.renderer.standard.double_axle;

import com.railwayteam.railways.content.custom_bogeys.renderer.standard.double_axle.base.DoubleAxleBogeyDisplay;
import com.railwayteam.railways.content.custom_bogeys.renderer.unified.ElementProvider;

import static com.railwayteam.railways.registry.CRBlockPartials.LONG_SHAFTED_WHEELS;
import static com.railwayteam.railways.registry.CRBlockPartials.PASSENGER_FRAME;

public class PassengerBogeyDisplay extends DoubleAxleBogeyDisplay {
    public PassengerBogeyDisplay(ElementProvider<?> prov) {
        super(prov, PASSENGER_FRAME, LONG_SHAFTED_WHEELS, true);
    }
}
