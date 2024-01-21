package com.railwayteam.railways.content.custom_bogeys.renderer.gauge.standard.single_axle;

import com.jozufozu.flywheel.api.MaterialManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.trains.bogey.BogeyRenderer;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.entity.CarriageBogey;
import net.minecraft.nbt.CompoundTag;

import static com.railwayteam.railways.registry.CRBlockPartials.*;

public class SingleaxleBogeyRenderer extends BogeyRenderer {
    @Override
    public void initialiseContraptionModelData(MaterialManager materialManager, CarriageBogey carriageBogey) {
        createModelInstance(materialManager, AllPartialModels.SMALL_BOGEY_WHEELS);
        createModelInstance(materialManager, SINGLEAXLE_FRAME);
    }

    @Override
    public BogeySizes.BogeySize getSize() {
        return BogeySizes.SMALL;
    }

    @Override
    public void render(CompoundTag bogeyData, float wheelAngle, PoseStack ms, int light, VertexConsumer vb, boolean inContraption) {
        boolean inInstancedContraption = vb == null;
        getTransform(SINGLEAXLE_FRAME, ms, inInstancedContraption)
                .render(ms, light, vb);

        getTransform(AllPartialModels.SMALL_BOGEY_WHEELS, ms, inInstancedContraption)
                .translate(0, 12 / 16f, 0)
                .rotateX(wheelAngle)
                .render(ms, light, vb);
    }
}
