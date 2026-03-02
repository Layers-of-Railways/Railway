/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2026 The Railways Team
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

package com.railwayteam.railways.base.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;

/*
 * Converts railways:${color}_locometal_smokebox[axis="z"] to railways:${color}_locometal_smokebox[facing="north"]
 * and converts railways:${color}_locometal_smokebox[axis="x"] to railways:${color}_locometal_smokebox[facing="east"]
 *
 * This is needed due to changing them from using axis to direction properties
 */
public class LocoMetalSmokeboxFacingFix extends AxisToFacingFix {
    public LocoMetalSmokeboxFacingFix(Schema outputSchema, String name) {
        super(outputSchema, name);
    }

    @Override
    protected boolean applyToBlockState(String blockId) {
        return blockId.matches("railways:(.*)_locometal_smokebox");
    }
}
