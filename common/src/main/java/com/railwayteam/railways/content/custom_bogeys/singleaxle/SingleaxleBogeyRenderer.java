package com.railwayteam.railways.content.custom_bogeys.singleaxle;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.util.transform.Transform;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.trains.bogey.BogeyRenderer;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import net.minecraft.nbt.CompoundTag;

import static com.railwayteam.railways.registry.CRBlockPartials.SINGLEAXLE_FRAME;
import static com.simibubi.create.AllPartialModels.SMALL_BOGEY_WHEELS;

public class SingleaxleBogeyRenderer extends BogeyRenderer {
    @Override
    public void initialiseContraptionModelData(MaterialManager materialManager) {
        createModelInstances(materialManager, SMALL_BOGEY_WHEELS, 1);
        createModelInstances(materialManager, SINGLEAXLE_FRAME);
    }

    @Override
    public BogeySizes.BogeySize getSize() {
        return BogeySizes.SMALL;
    }

    @Override
    public void render(CompoundTag bogeyData, float wheelAngle, PoseStack ms, int light, VertexConsumer vb, boolean inContraption) {
        boolean inInstancedContraption = vb == null;
        Transform<?> transform = getTransformFromPartial(SINGLEAXLE_FRAME, ms, inInstancedContraption);
        finalize(transform, ms, light, vb);

        Transform<?> bogeyWheels = getTransformFromPartial(SMALL_BOGEY_WHEELS, ms, inInstancedContraption)
                .translate(0, 12/16f, 0)
                .rotateX(wheelAngle);
        finalize(bogeyWheels, ms, light, vb);
    }
}
