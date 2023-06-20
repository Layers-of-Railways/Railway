package com.railwayteam.railways.content.custom_bogeys.singleaxle;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.util.transform.Transform;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.trains.bogey.BogeyRenderer;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.foundation.utility.Iterate;
import net.minecraft.nbt.CompoundTag;

import static com.simibubi.create.AllPartialModels.BOGEY_FRAME;
import static com.simibubi.create.AllPartialModels.SMALL_BOGEY_WHEELS;

public class SingleaxleBogeyRenderer extends BogeyRenderer {
    @Override
    public void initialiseContraptionModelData(MaterialManager materialManager) {
        createModelInstances(materialManager, SMALL_BOGEY_WHEELS, 2);
        createModelInstances(materialManager, BOGEY_FRAME);
    }

    @Override
    public BogeySizes.BogeySize getSize() {
        return BogeySizes.SMALL;
    }

    @Override
    public void render(CompoundTag bogeyData, float wheelAngle, PoseStack ms, int light, VertexConsumer vb, boolean inContraption) {
        boolean inInstancedContraption = vb == null;
        Transform<?> transform = getTransformFromPartial(BOGEY_FRAME, ms, inInstancedContraption);
        finalize(transform, ms, light, vb);

        Transform<?>[] wheels = getTransformsFromPartial(SMALL_BOGEY_WHEELS, ms, inInstancedContraption, 2);
        for (int side : Iterate.positiveAndNegative) {
            if (!inInstancedContraption)
                ms.pushPose();
            Transform<?> wheel = wheels[(side + 1)/2];
            wheel.translate(0, 12 / 16f, side)
                    .rotateX(wheelAngle);
            finalize(wheel, ms, light, vb);
            if (!inInstancedContraption)
                ms.popPose();
        }
    }
}
