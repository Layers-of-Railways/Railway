package com.railwayteam.railways.content.custom_bogeys.renderer.gauge.standard.large;

import com.jozufozu.flywheel.api.MaterialManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import com.simibubi.create.content.trains.bogey.BogeyRenderer;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.entity.CarriageBogey;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.Iterate;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

import static com.railwayteam.railways.registry.CRBlockPartials.*;

public class LargeCreateStyled080Renderer extends BogeyRenderer {
    @Override
    public void initialiseContraptionModelData(MaterialManager materialManager, CarriageBogey carriageBogey) {
        createModelInstance(materialManager,  LARGE_CREATE_STYLED_0_8_0_FRAME, LARGE_CREATE_STYLED_0_8_0_PISTON);
        createModelInstance(materialManager, LARGE_CREATE_STYLED_WHEELS, 2);
        createModelInstance(materialManager, AllPartialModels.LARGE_BOGEY_WHEELS, 2);
        createModelInstance(materialManager, AllPartialModels.BOGEY_PIN, 4);
        createModelInstance(materialManager, AllBlocks.SHAFT.getDefaultState()
                .setValue(ShaftBlock.AXIS, Direction.Axis.X), 2);
        createModelInstance(materialManager, AllBlocks.SHAFT.getDefaultState()
                .setValue(ShaftBlock.AXIS, Direction.Axis.Z), 4);
    }

    @Override
    public BogeySizes.BogeySize getSize() {
        return BogeySizes.LARGE;
    }

    @Override
    public void render(CompoundTag bogeyData, float wheelAngle, PoseStack ms, int light, VertexConsumer vb, boolean inContraption) {
        boolean inInstancedContraption = vb == null;

        BogeyModelData[] secondaryShafts = getTransform(AllBlocks.SHAFT.getDefaultState()
                .setValue(ShaftBlock.AXIS, Direction.Axis.X), ms, inInstancedContraption, 2);
        BogeyModelData[] middleShafts = getTransform(AllBlocks.SHAFT.getDefaultState()
                .setValue(ShaftBlock.AXIS, Direction.Axis.Z), ms, inInstancedContraption, 4);

        for (int i : Iterate.zeroAndOne) {
            secondaryShafts[i]
                    .translate(-.5f, .25f, 2.1835f + i * -5.365)
                    .centre()
                    .rotateX(wheelAngle)
                    .unCentre()
                    .render(ms, light, vb);
        }

        for (int side = 0; side < 4; side++) {
            middleShafts[side]
                    .translate(-.5f, .25f, 1.75f + side * -1.5)
                    .centre()
                    .rotateZ(wheelAngle)
                    .unCentre()
                    .render(ms, light, vb);
        }

        getTransform(LARGE_CREATE_STYLED_0_8_0_FRAME, ms, inInstancedContraption)
                .render(ms, light, vb);

        getTransform(LARGE_CREATE_STYLED_0_8_0_PISTON, ms, inInstancedContraption)
                .translate(0, 0, 1 / 4f * Math.sin(AngleHelper.rad(wheelAngle)))
                .render(ms, light, vb);

        BogeyModelData[] wheels = getTransform(AllPartialModels.LARGE_BOGEY_WHEELS, ms, inInstancedContraption, 2);
        BogeyModelData[] innerWheels = getTransform(LARGE_CREATE_STYLED_WHEELS, ms, inInstancedContraption, 2);
        BogeyModelData[] pins = getTransform(AllPartialModels.BOGEY_PIN, ms, inInstancedContraption, 4);

        if (!inInstancedContraption)
            ms.pushPose();

        for (int side : Iterate.positiveAndNegative) {
            BogeyModelData wheel = wheels[(side + 1) / 2];
            wheel.translate(0, 1, side * 2.6215)
                    .rotateX(wheelAngle)
                    .render(ms, light, vb);

            BogeyModelData innerWheel = innerWheels[(side + 1) / 2];
            innerWheel.translate(0, 1, side * .874)
                    .rotateX(wheelAngle)
                    .translate(0, -1, 0)
                    .render(ms, light, vb);;
        }

        for (int side = -1; side < 3; side++) {
            BogeyModelData pin = pins[side + 1];
            pin.translate(0, 1, -.8734f + side * 1.7466)
                    .rotateX(wheelAngle)
                    .translate(0, 1 / 4f, 0)
                    .rotateX(-wheelAngle)
                    .render(ms, light, vb);
        }

        if (!inInstancedContraption)
            ms.popPose();
    }
}
