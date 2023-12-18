package com.railwayteam.railways.content.custom_bogeys.renderer.gauge.standard.single_axle;

import com.jozufozu.flywheel.api.MaterialManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.trains.bogey.BogeyRenderer;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.entity.CarriageBogey;
import net.minecraft.nbt.CompoundTag;

import static com.railwayteam.railways.registry.CRBlockPartials.COILSPRING_FRAME;
import static com.railwayteam.railways.registry.CRBlockPartials.CR_BOGEY_WHEELS;

public class CoilspringBogeyRenderer extends BogeyRenderer {
    @Override
    public void initialiseContraptionModelData(MaterialManager materialManager, CarriageBogey carriageBogey) {
        createModelInstance(materialManager, CR_BOGEY_WHEELS, 1);
        createModelInstance(materialManager, COILSPRING_FRAME);
    }

    @Override
    public BogeySizes.BogeySize getSize() {
        return BogeySizes.SMALL;
    }

    @Override
    public void render(CompoundTag bogeyData, float wheelAngle, PoseStack ms, int light, VertexConsumer vb, boolean inContraption) {
        boolean inInstancedContraption = vb == null;
        getTransform(COILSPRING_FRAME, ms, inInstancedContraption)
                .translate(0, 5 / 16f, 0)
                .render(ms, light, vb);

        getTransform(CR_BOGEY_WHEELS, ms, inInstancedContraption)
                .translate(0, 12 / 16f, 0)
                .rotateX(wheelAngle)
                .translate(0, -7 / 16f, 0)
                .render(ms, light, vb);
    }
}