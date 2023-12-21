package com.railwayteam.railways.content.custom_bogeys.renderer.gauge.standard.medium;

import com.jozufozu.flywheel.api.MaterialManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import com.simibubi.create.content.trains.bogey.BogeyRenderer;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.entity.CarriageBogey;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

import static com.railwayteam.railways.registry.CRBlockPartials.MEDIUM_QUADRUPLE_WHEEL_FRAME;
import static com.railwayteam.railways.registry.CRBlockPartials.MEDIUM_SHARED_WHEELS;

public class MediumQuadrupleWheelRenderer extends BogeyRenderer {
    @Override
    public void initialiseContraptionModelData(MaterialManager materialManager, CarriageBogey carriageBogey) {
        createModelInstance(materialManager, MEDIUM_SHARED_WHEELS, 4);
        createModelInstance(materialManager, MEDIUM_QUADRUPLE_WHEEL_FRAME);
        createModelInstance(materialManager, AllBlocks.SHAFT.getDefaultState()
                .setValue(ShaftBlock.AXIS, Direction.Axis.Z), 4);
    }

    @Override
    public BogeySizes.BogeySize getSize() {
        return BogeySizes.SMALL;
    }

    @Override
    public void render(CompoundTag bogeyData, float wheelAngle, PoseStack ms, int light, VertexConsumer vb, boolean inContraption) {
        boolean inInstancedContraption = vb == null;

        BogeyModelData[] secondaryShafts = getTransform(AllBlocks.SHAFT.getDefaultState()
                .setValue(ShaftBlock.AXIS, Direction.Axis.Z), ms, inInstancedContraption, 4);

        for (int side = 0; side < 4; side++) {
            secondaryShafts[side]
                    .translate(-.5f, .31f, 1f + side * -1)
                    .centre()
                    .rotateZ(wheelAngle)
                    .unCentre()
                    .render(ms, light, vb);
        }

        getTransform(MEDIUM_QUADRUPLE_WHEEL_FRAME, ms, inInstancedContraption)
                .translate(0, 0 / 16f, 0)
                .render(ms, light, vb);

        BogeyModelData[] wheels = getTransform(MEDIUM_SHARED_WHEELS, ms, inInstancedContraption, 4);
        for (int side = -1; side < 3; side++) {
            if (!inInstancedContraption)
                ms.pushPose();
            BogeyModelData wheel = wheels[side + 1];
            wheel.translate(0, 13 / 16f, -.75f + side * 1.5)
                    .rotateX(wheelAngle)
                    .translate(0, -13 / 16f, 0)
                    .render(ms, light, vb);
            if (!inInstancedContraption)
                ms.popPose();
        }
    }
}
