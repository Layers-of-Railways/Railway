package com.railwayteam.railways.content.custom_bogeys.renderer.gauge.standard.medium;

import com.jozufozu.flywheel.api.MaterialManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.trains.bogey.BogeyRenderer;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.entity.CarriageBogey;
import net.minecraft.nbt.CompoundTag;

import static com.railwayteam.railways.registry.CRBlockPartials.*;

public class Medium202TrailingRenderer extends BogeyRenderer {
    @Override
    public void initialiseContraptionModelData(MaterialManager materialManager, CarriageBogey carriageBogey) {
        createModelInstance(materialManager, MEDIUM_2_0_2_TRAILING_WHEELS);
        createModelInstance(materialManager, MEDIUM_2_0_2_TRAILING_FRAME);
    }

    @Override
    public BogeySizes.BogeySize getSize() {
        return BogeySizes.SMALL;
    }

    @Override
    public void render(CompoundTag bogeyData, float wheelAngle, PoseStack ms, int light, VertexConsumer vb, boolean inContraption) {
        boolean inInstancedContraption = vb == null;
        getTransform(MEDIUM_2_0_2_TRAILING_FRAME, ms, inInstancedContraption)
                .translate(0, 0 / 16f, 0)
                .render(ms, light, vb);

        getTransform(MEDIUM_2_0_2_TRAILING_WHEELS, ms, inInstancedContraption)
                .translate(0, 12 / 16f, 0)
                .rotateX(wheelAngle)
                .translate(0, -13 / 16f, 0)
                .render(ms, light, vb);
    }
}
