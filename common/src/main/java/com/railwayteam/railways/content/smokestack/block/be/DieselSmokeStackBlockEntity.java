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

package com.railwayteam.railways.content.smokestack.block.be;

import com.railwayteam.railways.content.smokestack.ISpeedNotifiable;
import com.railwayteam.railways.content.smokestack.block.SmokeStackBlock;
import com.simibubi.create.content.contraptions.ContraptionWorld;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class DieselSmokeStackBlockEntity extends SmartBlockEntity implements ISpeedNotifiable {

    private LerpedFloat rpmLimiter;
    private double fanRotation;
    private double lastRotateTime;
    private double notifiedSpeed;

    public DieselSmokeStackBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}

    public double getRpm(float partialTicks) {
        boolean enabled = getBlockState().getValue(SmokeStackBlock.ENABLED);
        if (rpmLimiter == null) {
            rpmLimiter = LerpedFloat.linear().startWithValue(enabled ? 1 : 0);
        }
        rpmLimiter.chase(enabled ? 1 : 0, 0.05, LerpedFloat.Chaser.EXP);
        double speed = 32;
        if (level instanceof ContraptionWorld || level instanceof VirtualRenderWorld)
            speed = notifiedSpeed;
        return speed * rpmLimiter.getValue(partialTicks);
    }

    @Override
    public void tick() {
        super.tick();
        if (rpmLimiter != null)
            rpmLimiter.tickChaser();
    }

    public double getFanRotation(double rpm) {
        double currentTime = level == null ? 0 : AnimationTickHolder.getRenderTime(level);
        double delta = currentTime - lastRotateTime;
        lastRotateTime += delta;
        //minutes = delta / 20 / 60
        double movementAmt = (delta / 20 / 60) * rpm * 360;
        fanRotation += movementAmt;
        while (fanRotation > 360)
            fanRotation -= 360;
        while (fanRotation < 0)
            fanRotation += 360;
        return fanRotation;
    }

    @Override
    public void notifySpeed(double speed) {
        this.notifiedSpeed = 112 / (1 + Math.pow(2, -16 * speed + 10)) + 48;
    }
}
