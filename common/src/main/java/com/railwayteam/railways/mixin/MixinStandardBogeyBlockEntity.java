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

package com.railwayteam.railways.mixin;

import com.railwayteam.railways.mixin_interfaces.IStandardBogeyTEVirtualCoupling;
import com.simibubi.create.content.trains.bogey.StandardBogeyBlockEntity;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = StandardBogeyBlockEntity.class, remap = false)
public class MixinStandardBogeyBlockEntity implements IStandardBogeyTEVirtualCoupling {
    private double coupling = -1;
    @Override
    public void setCouplingDistance(double distance) {
        coupling = distance;
    }

    @Override
    public double getCouplingDistance() {
        return coupling;
    }

    private Direction direction = Direction.UP;

    @Override
    public void setCouplingDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    public Direction getCouplingDirection() {
        return direction;
    }

    private boolean front = false;

    @Override
    public void setFront(boolean front) {
        this.front = front;
    }

    @Override
    public boolean getFront() {
        return front;
    }
}
