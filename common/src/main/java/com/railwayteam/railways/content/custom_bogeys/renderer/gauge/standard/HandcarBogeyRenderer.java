package com.railwayteam.railways.content.custom_bogeys.renderer.gauge.standard;

import com.jozufozu.flywheel.api.MaterialManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.railwayteam.railways.content.handcar.ik.DoubleArmIK;
import com.simibubi.create.content.trains.bogey.BogeyRenderer;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.entity.CarriageBogey;
import com.simibubi.create.foundation.utility.Iterate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import static com.railwayteam.railways.registry.CRBlockPartials.*;
import static com.railwayteam.railways.registry.CRBlockPartials.CR_BOGEY_WHEELS;

public class HandcarBogeyRenderer extends BogeyRenderer {
    @Override
    public void initialiseContraptionModelData(MaterialManager materialManager, CarriageBogey carriageBogey) {
        createModelInstance(materialManager, CR_BOGEY_WHEELS, 2);
        createModelInstance(materialManager, HANDCAR_COUPLING);
        createModelInstance(materialManager, HANDCAR_FRAME);
        createModelInstance(materialManager, HANDCAR_HANDLE);
        createModelInstance(materialManager, HANDCAR_LARGE_COG);
        createModelInstance(materialManager, HANDCAR_SMALL_COG);
    }

    @Override
    public BogeySizes.BogeySize getSize() {
        return BogeySizes.SMALL;
    }

    @Override
    public void render(CompoundTag bogeyData, float wheelAngle, PoseStack ms, int light, VertexConsumer vb, boolean inContraption) {
//            wheelAngle = AnimationTickHolder.getTicks(true) + AnimationTickHolder.getPartialTicks();
        wheelAngle *= 2;
        boolean inInstancedContraption = vb == null;

        getTransform(HANDCAR_FRAME, ms, inInstancedContraption)
                .translate(0, 5 / 16f, 0)
                .render(ms, light, vb);

        Vec3 coupling_pos;
        {
            final double couple_r = (3 / 16.) * Mth.SQRT_OF_TWO;
            final double couple_degrees = (-wheelAngle / 2) - 22.5;
            float couple_radians = (float) (couple_degrees * Mth.DEG_TO_RAD);
            double couple_x = couple_r * Mth.sin(couple_radians);
            double couple_y = couple_r * Mth.cos(couple_radians);
            coupling_pos = new Vec3(1.75 / 16., (12 / 16.) + couple_y, (-3.5 / 16.) + couple_x);
        }

        Vec2 upperVec2 = new Vec2(0, 39 / 16f);
        Vec2 couplingVec2 = new Vec2((float)coupling_pos.z, (float)coupling_pos.y);

        //                                                                             upper         lower
        Vec2 hingeOffset = DoubleArmIK.calculateJointOffset(upperVec2, couplingVec2, 14 / 16., 18 / 16.);
        Vec2 hingePos2 = hingeOffset.add(couplingVec2);

        double couplingAngle;
        double handleAngle;

        {
            couplingAngle = Mth.atan2((hingeOffset.y), (hingeOffset.x));

            Vec2 handle_offset = hingePos2.add(upperVec2.negated());
            handleAngle = Mth.atan2(handle_offset.y, handle_offset.x);
        }

        getTransform(HANDCAR_HANDLE, ms, inInstancedContraption)
                .translateY(39 / 16.)
                .rotateZ(180)
                .rotateXRadians(handleAngle - Math.toRadians(90-32.5))
                .translateY(-34 / 16.)
                .render(ms, light, vb);

        getTransform(HANDCAR_COUPLING, ms, inInstancedContraption)
                .translate(coupling_pos)
                .rotateXRadians(-(couplingAngle - Mth.HALF_PI))
                .render(ms, light, vb);

            /*
            getTransform(AllPartialModels.BOGEY_PIN, ms, inInstancedContraption)
                .translateY(hingePos2.y)
                .translateZ(hingePos2.x)
                .scale(0.5f, 2.0f, 2.0f)
                .render(ms, light, vb);

            getTransform(AllPartialModels.BOGEY_PIN, ms, inInstancedContraption)
                .translateY(upperVec2.y)
                .translateZ(upperVec2.x)
                .scale(0.5f, 1.0f, 1.0f)
                .render(ms, light, vb);*/

        getTransform(HANDCAR_LARGE_COG, ms, inInstancedContraption)
                .translate(-8 / 16f, 12 / 16f, -3.5 / 16f)
                .rotateX((-wheelAngle / 2) + 22.5)
                .rotateZ(90)
                .translate(0, -7 / 16f, 0)
                .render(ms, light, vb);

        getTransform(HANDCAR_SMALL_COG, ms, inInstancedContraption)
                .translate(-8 / 16f, 12 / 16f, -1)
                .rotateX(wheelAngle)
                .rotateZ(90)
                .translate(0, -7 / 16f, 0)
                .render(ms, light, vb);

        BogeyModelData[] wheels = getTransform(CR_BOGEY_WHEELS, ms, inInstancedContraption, 2);
        for (int side : Iterate.positiveAndNegative) {
            if (!inInstancedContraption)
                ms.pushPose();
            wheels[(side + 1) / 2]
                    .translate(0, 12 / 16f, side)
                    .rotateX(wheelAngle)
                    .translate(0, -7 / 16f, 0)
                    .render(ms, light, vb);
            if (!inInstancedContraption)
                ms.popPose();
        }
    }
}
