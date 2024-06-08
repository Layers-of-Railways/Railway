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

import com.railwayteam.railways.mixin_interfaces.IPreAssembleCallback;
import com.simibubi.create.content.trains.signal.TrackEdgePoint;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TrackTargetingBehaviour.class)
public abstract class MixinTrackTargetingBehaviour<T extends TrackEdgePoint> extends BlockEntityBehaviour implements IPreAssembleCallback {
    @Shadow(remap = false) private T edgePoint;

    private MixinTrackTargetingBehaviour(SmartBlockEntity be) {
        super(be);
    }

    @Shadow public abstract Direction.AxisDirection getTargetDirection();

    @Override
    public void railways$preAssemble() {
        if (edgePoint != null && !getWorld().isClientSide)
            edgePoint.blockEntityRemoved(getPos(), getTargetDirection() == Direction.AxisDirection.POSITIVE);
    }
}
