/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.content.custom_bogeys.renderer.standard.triple_axle;

import com.jozufozu.flywheel.api.MaterialManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.trains.bogey.BogeyRenderer;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.entity.CarriageBogey;
import net.minecraft.nbt.CompoundTag;

import static com.railwayteam.railways.registry.CRBlockPartials.CR_BOGEY_WHEELS;
import static com.railwayteam.railways.registry.CRBlockPartials.RADIAL_FRAME;

public class RadialBogeyRenderer extends BogeyRenderer {
    @Override
    public void initialiseContraptionModelData(MaterialManager materialManager, CarriageBogey carriageBogey) {
        createModelInstance(materialManager, CR_BOGEY_WHEELS, 3);
        createModelInstance(materialManager, RADIAL_FRAME);
    }

    @Override
    public BogeySizes.BogeySize getSize() {
        return BogeySizes.SMALL;
    }

    @Override
    public void render(CompoundTag bogeyData, float wheelAngle, PoseStack ms, int light, VertexConsumer vb, boolean inContraption) {
        boolean inInstancedContraption = vb == null;
        getTransform(RADIAL_FRAME, ms, inInstancedContraption)
                .translate(0, 5 / 16f, 0)
                .render(ms, light, vb);

        BogeyModelData[] wheels = getTransform(CR_BOGEY_WHEELS, ms, inInstancedContraption, 3);
        for (int side = -1; side < 2; side++) {
            if (!inInstancedContraption)
                ms.pushPose();
            BogeyModelData wheel = wheels[side + 1];
            wheel.translate(0, 12 / 16f, side*1.5)
                    .rotateX(wheelAngle)
                    .translate(0, -7 / 16f, 0)
                    .render(ms, light, vb);
            if (!inInstancedContraption)
                ms.popPose();
        }
    }
}
