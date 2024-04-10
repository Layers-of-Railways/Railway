package com.railwayteam.railways.content.custom_bogeys.special.monobogey;

import com.jozufozu.flywheel.api.MaterialManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import com.simibubi.create.content.trains.bogey.BogeyRenderer;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.entity.CarriageBogey;
import com.simibubi.create.foundation.utility.Iterate;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

import static com.railwayteam.railways.registry.CRBlockPartials.*;
import static com.simibubi.create.content.trains.entity.CarriageBogey.UPSIDE_DOWN_KEY;

public class MonoBogeyRenderer extends BogeyRenderer {
    @Override
    public void initialiseContraptionModelData(MaterialManager materialManager, CarriageBogey carriageBogey) {
        createModelInstance(materialManager, MONOBOGEY_FRAME);
        createModelInstance(materialManager, MONOBOGEY_WHEEL, 4);
        createModelInstance(materialManager, AllBlocks.SHAFT.getDefaultState()
                .setValue(ShaftBlock.AXIS, Direction.Axis.Z), 4);
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

        BogeyModelData[] shafts = getTransform(AllBlocks.SHAFT.getDefaultState()
                .setValue(ShaftBlock.AXIS, Direction.Axis.Z), ms, inInstancedContraption, 4);

        for (boolean left : Iterate.trueAndFalse) {
            for (int front : Iterate.positiveAndNegative) {
                BogeyModelData shaft = shafts[(left ? 1 : 0) + (front + 1)];
                shaft.translate(left ? -21 / 16f : 5 / 16f, specialUpsideDown ? 32 / 16f : 0 / 16f, -.5f + front * 8 / 16f)
                        .centre()
                        .rotateZ(left ? wheelAngle : -wheelAngle)
                        .unCentre()
                        .render(ms, light, vb);
            }
        }

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
                wheel.translate(left ? -13 / 16f : 13 / 16f, specialUpsideDown ? 32 / 16f : 0 / 16f, front * 16 / 16f)
                        .rotateY(left ? wheelAngle : -wheelAngle)
                        .translate(13 / 16f, 0, 16 / 16f)
                        .render(ms, light, vb);
                if (!inInstancedContraption)
                    ms.popPose();
            }
        }
    }
}