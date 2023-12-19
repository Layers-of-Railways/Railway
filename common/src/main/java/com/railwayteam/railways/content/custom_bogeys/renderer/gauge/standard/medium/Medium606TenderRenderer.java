package com.railwayteam.railways.content.custom_bogeys.renderer.gauge.standard.medium;

import com.jozufozu.flywheel.api.MaterialManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.trains.bogey.BogeyRenderer;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.entity.CarriageBogey;
import net.minecraft.nbt.CompoundTag;

import static com.railwayteam.railways.registry.CRBlockPartials.*;

public class Medium606TenderRenderer extends BogeyRenderer {
    @Override
    public void initialiseContraptionModelData(MaterialManager materialManager, CarriageBogey carriageBogey) {
        createModelInstance(materialManager, MEDIUM_SHARED_WHEELS, 3);
        createModelInstance(materialManager, MEDIUM_6_0_6_TENDER_FRAME);
    }

    @Override
    public BogeySizes.BogeySize getSize() {
        return BogeySizes.SMALL;
    }

    @Override
    public void render(CompoundTag bogeyData, float wheelAngle, PoseStack ms, int light, VertexConsumer vb, boolean inContraption) {
        boolean inInstancedContraption = vb == null;
        getTransform(MEDIUM_6_0_6_TENDER_FRAME, ms, inInstancedContraption)
                .translate(0, 0 / 16f, 0)
                .render(ms, light, vb);

        BogeyModelData[] wheels = getTransform(MEDIUM_SHARED_WHEELS, ms, inInstancedContraption, 3);
        for (int side = -1; side < 2; side++) {
            if (!inInstancedContraption)
                ms.pushPose();
            BogeyModelData wheel = wheels[side + 1];
            wheel.translate(0, 13 / 16f, side*1.5)
                    .rotateX(wheelAngle)
                    .translate(0, -13 / 16f, 0)
                    .render(ms, light, vb);
            if (!inInstancedContraption)
                ms.popPose();
        }
    }
}
