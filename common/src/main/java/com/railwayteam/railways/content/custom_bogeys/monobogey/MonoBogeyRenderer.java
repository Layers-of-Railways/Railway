package com.railwayteam.railways.content.custom_bogeys.monobogey;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.util.transform.Transform;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.trains.bogey.BogeyRenderer;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.entity.CarriageBogey;
import com.simibubi.create.foundation.utility.Iterate;
import net.minecraft.nbt.CompoundTag;

import static com.railwayteam.railways.registry.CRBlockPartials.MONOBOGEY_FRAME;
import static com.railwayteam.railways.registry.CRBlockPartials.MONOBOGEY_WHEEL;
import static com.simibubi.create.content.trains.entity.CarriageBogey.UPSIDE_DOWN_KEY;

public class MonoBogeyRenderer {
    public static class SmallMonoBogeyRenderer extends BogeyRenderer {

        @Override
        public void initialiseContraptionModelData(MaterialManager materialManager, CarriageBogey carriageBogey) {
            createModelInstance(materialManager, MONOBOGEY_WHEEL, 4);
            createModelInstance(materialManager, MONOBOGEY_FRAME);
        }

        @Override
        public BogeySizes.BogeySize getSize() {
            return BogeySizes.SMALL;
        }

        @Override
        public void render(CompoundTag bogeyData, float wheelAngle, PoseStack ms, int light, VertexConsumer vb, boolean inContraption) {
            boolean upsideDown = bogeyData.getBoolean(UPSIDE_DOWN_KEY);
            boolean inInstancedContraption = vb == null;
            boolean specialUpsideDown = !inContraption && upsideDown; // tile entity renderer needs special handling
            getTransform(MONOBOGEY_FRAME, ms, inInstancedContraption)
                .rotateZ(specialUpsideDown ? 180 : 0)
                .translateY(specialUpsideDown ? -3 : 0)
                .render(ms, light, vb);

            BogeyModelData[] wheels = getTransform(MONOBOGEY_WHEEL, ms, inInstancedContraption, 4);
            for (boolean left : Iterate.trueAndFalse) {
                for (int front : Iterate.positiveAndNegative) {
                    if (!inInstancedContraption)
                        ms.pushPose();
                    BogeyModelData wheel = wheels[(left ? 1 : 0) + (front + 1)];
                    wheel.translate(left ? -12 / 16f : 12 / 16f, specialUpsideDown ? 35 /16f : 3 / 16f, front * 15 / 16f) //base position
                        .rotateY(left ? wheelAngle : -wheelAngle)
                        .translate(15/16f, 0, 0/16f)
                        .render(ms, light, vb);
                    if (!inInstancedContraption)
                        ms.popPose();
                }
            }
        }
    }
}