/*
 * Steam 'n' Rails
 * Copyright (c) 2025-2026 The Railways Team
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

public class DiagonalHazardStripesFacingFix extends AxisToFacingFix {
    public DiagonalHazardStripesFacingFix(Schema outputSchema, String name) {
        super(outputSchema, name);
    }

    @Override
    protected boolean applyToBlockState(String blockId) {
        if (!blockId.startsWith("railways:")) return false;

        return blockId.endsWith("_hazard_stripes_diagonal_on_black")
            || blockId.endsWith("_hazard_stripes_diagonal_on_white");
    }
}
