package com.railwayteam.railways.content.custom_bogeys.renderer.gauge.standard.triple_axle;

import com.jozufozu.flywheel.api.MaterialManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.trains.bogey.BogeyRenderer;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.entity.CarriageBogey;
import net.minecraft.nbt.CompoundTag;

import static com.railwayteam.railways.registry.CRBlockPartials.CR_BOGEY_WHEELS;
import static com.railwayteam.railways.registry.CRBlockPartials.HEAVYWEIGHT_FRAME;

public class HeavyweightBogeyRenderer extends BogeyRenderer {
    @Override
    public void initialiseContraptionModelData(MaterialManager materialManager, CarriageBogey carriageBogey) {
        createModelInstance(materialManager, CR_BOGEY_WHEELS, 3);
        createModelInstance(materialManager, HEAVYWEIGHT_FRAME);
    }

    @Override
    public BogeySizes.BogeySize getSize() {
        return BogeySizes.SMALL;
    }

    @Override
    public void render(CompoundTag bogeyData, float wheelAngle, PoseStack ms, int light, VertexConsumer vb, boolean inContraption) {
        boolean inInstancedContraption = vb == null;
        getTransform(HEAVYWEIGHT_FRAME, ms, inInstancedContraption)
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
