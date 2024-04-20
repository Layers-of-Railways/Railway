package com.railwayteam.railways.content.custom_bogeys.renderer.wide;

import com.jozufozu.flywheel.api.MaterialManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import com.simibubi.create.content.trains.bogey.BogeyRenderer;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.entity.CarriageBogey;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.Iterate;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

import static com.railwayteam.railways.registry.CRBlockPartials.*;
import static com.railwayteam.railways.registry.CRBlockPartials.WIDE_COMICALLY_LARGE_PINS;

public class WideComicallyLargeScotchYokeBogeyRenderer extends BogeyRenderer {
    @Override
    public void initialiseContraptionModelData(MaterialManager materialManager, CarriageBogey carriageBogey) {
        createModelInstance(materialManager, WIDE_COMICALLY_LARGE_FRAME, WIDE_COMICALLY_LARGE_WHEELS,
                WIDE_COMICALLY_LARGE_PINS, WIDE_COMICALLY_LARGE_PISTONS);
        createModelInstance(materialManager, AllBlocks.SHAFT.getDefaultState()
                .setValue(ShaftBlock.AXIS, Direction.Axis.Z), 2);
        createModelInstance(materialManager, AllBlocks.SHAFT.getDefaultState()
                .setValue(ShaftBlock.AXIS, Direction.Axis.X), 4);
    }

    @Override
    public BogeySizes.BogeySize getSize() {
        return BogeySizes.LARGE;
    }

    @Override
    public void render(CompoundTag bogeyData, float wheelAngle, PoseStack ms, int light, VertexConsumer vb, boolean inContraption) {
        boolean inInstancedContraption = vb == null;

        BogeyModelData[] primaryShafts = getTransform(AllBlocks.SHAFT.getDefaultState()
                .setValue(ShaftBlock.AXIS, Direction.Axis.Z), ms, inInstancedContraption, 2);

        for (int i : Iterate.zeroAndOne) {
            primaryShafts[i].translate(-.5, 4 / 16., i * -1)
                    .centre()
                    .rotateZ(wheelAngle)
                    .unCentre()
                    .render(ms, light, vb);
        }

        BogeyModelData[] secondaryShafts = getTransform(AllBlocks.SHAFT.getDefaultState()
                .setValue(ShaftBlock.AXIS, Direction.Axis.X), ms, inInstancedContraption, 4);

        for (int i : Iterate.zeroAndOne) {
            for (int side : Iterate.zeroAndOne) {
                secondaryShafts[i + (side * 2)]
                        .translate(-1 + side, 4 / 16., (10 / 16.) + i * -(36 / 16.))
                        .centre()
                        .rotateX(wheelAngle)
                        .unCentre()
                        .render(ms, light, vb);
            }
        }

        getTransform(WIDE_COMICALLY_LARGE_FRAME, ms, inInstancedContraption)
                .translate(0, 4 / 16., 0)
                .render(ms, light, vb);

        getTransform(WIDE_COMICALLY_LARGE_PISTONS, ms, inInstancedContraption)
                .translate(0, 1.5, (1 / 4f + (5 / 16.)) * Math.sin(AngleHelper.rad(wheelAngle)))
                .render(ms, light, vb);

        if (!inInstancedContraption)
            ms.pushPose();

        getTransform(WIDE_COMICALLY_LARGE_WHEELS, ms, inInstancedContraption)
                .translate(0, 1.5, 0)
                .rotateX(wheelAngle)
                .translate(0, 0, 0)
                .render(ms, light, vb);

        getTransform(WIDE_COMICALLY_LARGE_PINS, ms, inInstancedContraption)
                .translate(0, 1.5, 0)
                .rotateX(wheelAngle)
                .translate(0, 1 / 4f + (5 / 16.), 0)
                .rotateX(-wheelAngle)
                .render(ms, light, vb);

        if (!inInstancedContraption)
            ms.popPose();
    }
}
