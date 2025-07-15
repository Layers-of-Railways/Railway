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

package com.railwayteam.railways.content.custom_bogeys.special.monobogey;

import com.railwayteam.railways.content.custom_bogeys.renderer.unified.BogeyDisplay;
import com.railwayteam.railways.content.custom_bogeys.renderer.unified.ElementProvider;
import com.simibubi.create.AllPartialModels;
import dev.engine_room.flywheel.lib.transform.Affine;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

import static com.railwayteam.railways.registry.CRBlockPartials.MONOBOGEY_FRAME;
import static com.railwayteam.railways.registry.CRBlockPartials.MONOBOGEY_WHEEL;
import static com.simibubi.create.content.trains.entity.CarriageBogey.UPSIDE_DOWN_KEY;

public class MonoBogeyDisplay implements BogeyDisplay {
    private final Affine<?> frame;
    private final Affine<?>[] wheels;
    private final Affine<?>[] shafts;

    private final boolean inContraption;

    public MonoBogeyDisplay(ElementProvider<?> prov, boolean inContraption) {
        this.inContraption = inContraption;

        frame = prov.create(MONOBOGEY_FRAME);
        wheels = prov.create(MONOBOGEY_WHEEL, 4);
        shafts = prov.create(AllPartialModels.SHAFT, 4);
    }

    @Override
    public void update(CompoundTag bogeyData, float wheelAngle) {
        boolean upsideDown = bogeyData.getBoolean(UPSIDE_DOWN_KEY);
        boolean specialUpsideDown = !inContraption && upsideDown; // tile entity renderer needs special handling

        frame.rotateZDegrees(specialUpsideDown ? 180 : 0)
            .translateY(specialUpsideDown ? -3 : 0);

        for (boolean left : Iterate.trueAndFalse) {
            for (int front : Iterate.positiveAndNegative) {
                int i = (left ? 1 : 0) + (front + 1);

                Affine<?> shaft = shafts[i];
                shaft.translate(left ? -21 / 16f : 5 / 16f, specialUpsideDown ? 32 / 16f : 0 / 16f, -.5f + front * 8 / 16f)
                    .center()
                    .rotateTo(Direction.UP, Direction.SOUTH)
                    .rotateYDegrees(left ? wheelAngle : -wheelAngle)
                    .uncenter();

                Affine<?> wheel = wheels[i];
                wheel.translate(left ? -13 / 16f : 13 / 16f, specialUpsideDown ? 32 / 16f : 0 / 16f, front * 16 / 16f)
                    .rotateYDegrees(left ? wheelAngle : -wheelAngle)
                    .translate(13 / 16f, 0, 16 / 16f);
            }
        }
    }
}
