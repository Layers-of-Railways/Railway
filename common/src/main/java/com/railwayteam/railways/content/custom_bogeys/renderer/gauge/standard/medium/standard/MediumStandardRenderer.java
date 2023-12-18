package com.railwayteam.railways.content.custom_bogeys.renderer.gauge.standard.medium.standard;

import com.jozufozu.flywheel.api.MaterialManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.trains.bogey.BogeyRenderer;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.entity.CarriageBogey;
import com.simibubi.create.foundation.utility.Iterate;
import net.minecraft.nbt.CompoundTag;

import static com.railwayteam.railways.registry.CRBlockPartials.MEDIUM_STANDARD_FRAME;
import static com.railwayteam.railways.registry.CRBlockPartials.MEDIUM_STANDARD_WHEELS;

public class MediumStandardRenderer extends BogeyRenderer {
    @Override
    public void initialiseContraptionModelData(MaterialManager materialManager, CarriageBogey carriageBogey) {
        createModelInstance(materialManager, MEDIUM_STANDARD_WHEELS, 2);
        createModelInstance(materialManager, MEDIUM_STANDARD_FRAME);
    }

    @Override
    public BogeySizes.BogeySize getSize() {
        return BogeySizes.SMALL;
    }

    @Override
    public void render(CompoundTag bogeyData, float wheelAngle, PoseStack ms, int light, VertexConsumer vb, boolean inContraption) {
        boolean inInstancedContraption = vb == null;
        getTransform(MEDIUM_STANDARD_FRAME, ms, inInstancedContraption)
                .translate(0, 0 / 16f, 0)
                .render(ms, light, vb);

        BogeyModelData[] wheels = getTransform(MEDIUM_STANDARD_WHEELS, ms, inInstancedContraption, 2);
        for (int side : Iterate.positiveAndNegative) {
            if (!inInstancedContraption)
                ms.pushPose();
            BogeyModelData wheel = wheels[(side + 1) / 2];
            wheel.translate(0, 13 / 16f, side)
                    .rotateX(wheelAngle)
                    .translate(0, -13 / 16f, -1)
                    .render(ms, light, vb);
            if (!inInstancedContraption)
                ms.popPose();
        }
    }
}
