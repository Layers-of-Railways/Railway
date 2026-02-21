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

package com.railwayteam.railways.content.animated_flywheel;

import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld;
import com.railwayteam.railways.config.CRConfigs;
import com.railwayteam.railways.mixin_interfaces.ICarriageFlywheel;
import com.railwayteam.railways.mixin_interfaces.IDistanceTravelled;
import com.simibubi.create.content.contraptions.behaviour.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.content.kinetics.flywheel.FlywheelBlockEntity;
import com.simibubi.create.content.trains.entity.CarriageContraption;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class FlywheelMovementBehaviour implements MovementBehaviour {
    @Override
    public boolean renderAsNormalBlockEntity() {
        return true;
    }

    private static class TemporaryData {
        float wheelAngle0 = Float.NaN;
        float wheelAngle1 = Float.NaN;
    }

    @Override
    public void tick(MovementContext context) {
        Object temporaryDataBackup = context.temporaryData;
        context.temporaryData = null;

        // Early exit checks, don't mind them :)
        if (!CRConfigs.client().animatedFlywheels.get()) return;
        if (!context.world.isClientSide || Minecraft.getInstance().isPaused()) return;
        if (!(context.contraption instanceof CarriageContraption carriageContraption)) return;
        if (!(carriageContraption.entity instanceof CarriageContraptionEntity carriageContraptionEntity)) return;
        if (!(context.contraption.presentBlockEntities.get(context.localPos) instanceof FlywheelBlockEntity flywheelBlockEntity)) return;
        if (flywheelBlockEntity.getBlockState().getValue(BlockStateProperties.AXIS).isVertical()) return;
        // It wasn't that bad was it? :^)

        Direction dir = carriageContraption.getAssemblyDirection();
        Direction.Axis flwAxis = flywheelBlockEntity.getBlockState().getValue(BlockStateProperties.AXIS);

        switch (dir) {
            case NORTH, SOUTH -> { if (flwAxis == Direction.Axis.Z) return; }
            case EAST, WEST -> { if (flwAxis == Direction.Axis.X) return; }
        }

        TemporaryData td = temporaryDataBackup instanceof TemporaryData tempDat ? tempDat : new TemporaryData();
        context.temporaryData = td;

        ICarriageFlywheel flywheel = ((ICarriageFlywheel) flywheelBlockEntity);
        double distanceTravelled = ((IDistanceTravelled) carriageContraptionEntity).railways$getDistanceTravelled();

        double wheelRadius = 1.4375;
        double wheelCircumference = 2 * Math.PI * wheelRadius;
        double angleDiff = 360 * (distanceTravelled / wheelCircumference);

        if (dir == Direction.SOUTH || dir == Direction.WEST)
            angleDiff = -angleDiff;

        float currentAngle = Float.isNaN(td.wheelAngle1) ? flywheel.railways$getAngle() : td.wheelAngle1;
        td.wheelAngle0 = (currentAngle + 360) % 360;
        td.wheelAngle1 = (float) (td.wheelAngle0 + angleDiff);
    }

    @Override
    public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
        if (!(context.contraption.presentBlockEntities.get(context.localPos) instanceof FlywheelBlockEntity flywheelBlockEntity)) return;
        if (!(context.temporaryData instanceof TemporaryData temporaryData)) return;

        ((ICarriageFlywheel) flywheelBlockEntity).railways$setAngle(Mth.lerp(
            AnimationTickHolder.getPartialTicks(),
            temporaryData.wheelAngle0,
            temporaryData.wheelAngle1
        ));
    }
}
