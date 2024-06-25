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

package com.railwayteam.railways.content.custom_bogeys.blocks.standard.large;

import com.railwayteam.railways.content.custom_bogeys.blocks.base.size.LargeBogeyBlock;
import com.railwayteam.railways.registry.CRBogeyStyles;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import net.minecraft.world.phys.Vec3;

public class LargeCreateStyle0120BogeyBlock extends LargeBogeyBlock {
    public LargeCreateStyle0120BogeyBlock(Properties props) {
        super(props, CRBogeyStyles.LARGE_CREATE_STYLED_0_12_0, BogeySizes.LARGE);
    }

    @Override
    public Vec3 getConnectorAnchorOffset() {
        return new Vec3(0, 7 / 32f, 172 / 32f);
    }

    @Override
    public double getWheelPointSpacing() {
        return 6;
    }
}
