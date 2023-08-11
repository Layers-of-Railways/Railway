package com.railwayteam.railways.content.custom_bogeys;

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

public class CRBogeyRenderer {

    //SINGLEAXLES:

    public static class SingleaxleBogeyRenderer extends BogeyRenderer {
        @Override
        public void initialiseContraptionModelData(MaterialManager materialManager, CarriageBogey carriageBogey) {
            createModelInstance(materialManager, CR_BOGEY_WHEELS, 1);
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
                    .translate(0, 5 / 16f, 0)
                    .render(ms, light, vb);

            getTransform(CR_BOGEY_WHEELS, ms, inInstancedContraption)
                    .translate(0, 12 / 16f, 0)
                    .rotateX(wheelAngle)
                    .translate(0, -7 / 16f, 0)
                    .render(ms, light, vb);
        }
    }

    public static class LeafspringBogeyRenderer extends BogeyRenderer {
        @Override
        public void initialiseContraptionModelData(MaterialManager materialManager, CarriageBogey carriageBogey) {
            createModelInstance(materialManager, CR_BOGEY_WHEELS, 1);
            createModelInstance(materialManager, LEAFSPRING_FRAME);
        }

        @Override
        public BogeySizes.BogeySize getSize() {
            return BogeySizes.SMALL;
        }

        @Override
        public void render(CompoundTag bogeyData, float wheelAngle, PoseStack ms, int light, VertexConsumer vb, boolean inContraption) {
            boolean inInstancedContraption = vb == null;
            getTransform(LEAFSPRING_FRAME, ms, inInstancedContraption)
                    .translate(0, 5 / 16f, 0)
                    .render(ms, light, vb);

            getTransform(CR_BOGEY_WHEELS, ms, inInstancedContraption)
                    .translate(0, 12 / 16f, 0)
                    .rotateX(wheelAngle)
                    .translate(0, -7 / 16f, 0)
                    .render(ms, light, vb);
        }
    }

    public static class CoilspringBogeyRenderer extends BogeyRenderer {
        @Override
        public void initialiseContraptionModelData(MaterialManager materialManager, CarriageBogey carriageBogey) {
            createModelInstance(materialManager, CR_BOGEY_WHEELS, 1);
            createModelInstance(materialManager, COILSPRING_FRAME);
        }

        @Override
        public BogeySizes.BogeySize getSize() {
            return BogeySizes.SMALL;
        }

        @Override
        public void render(CompoundTag bogeyData, float wheelAngle, PoseStack ms, int light, VertexConsumer vb, boolean inContraption) {
            boolean inInstancedContraption = vb == null;
            getTransform(COILSPRING_FRAME, ms, inInstancedContraption)
                    .translate(0, 5 / 16f, 0)
                    .render(ms, light, vb);

            getTransform(CR_BOGEY_WHEELS, ms, inInstancedContraption)
                    .translate(0, 12 / 16f, 0)
                    .rotateX(wheelAngle)
                    .translate(0, -7 / 16f, 0)
                    .render(ms, light, vb);
        }
    }

    //TWOAXLESONE:

    public static class FreightBogeyRenderer extends BogeyRenderer {
        @Override
        public void initialiseContraptionModelData(MaterialManager materialManager, CarriageBogey carriageBogey) {
            createModelInstance(materialManager, CR_BOGEY_WHEELS, 2);
            createModelInstance(materialManager, FREIGHT_FRAME);
        }

        @Override
        public BogeySizes.BogeySize getSize() {
            return BogeySizes.SMALL;
        }

        @Override
        public void render(CompoundTag bogeyData, float wheelAngle, PoseStack ms, int light, VertexConsumer vb, boolean inContraption) {
            boolean inInstancedContraption = vb == null;
            getTransform(FREIGHT_FRAME, ms, inInstancedContraption)
                    .translate(0, 5 / 16f, 0)
                    .render(ms, light, vb);

            BogeyModelData[] wheels = getTransform(CR_BOGEY_WHEELS, ms, inInstancedContraption, 2);
            for (int side : Iterate.positiveAndNegative) {
                if (!inInstancedContraption)
                    ms.pushPose();
                BogeyModelData wheel = wheels[(side + 1) / 2];
                wheel.translate(0, 12 / 16f, side)
                        .rotateX(wheelAngle)
                        .translate(0, -7 / 16f, 0)
                        .render(ms, light, vb);
                if (!inInstancedContraption)
                    ms.popPose();
            }
        }
    }

    public static class ArchbarBogeyRenderer extends BogeyRenderer {
        @Override
        public void initialiseContraptionModelData(MaterialManager materialManager, CarriageBogey carriageBogey) {
            createModelInstance(materialManager, CR_BOGEY_WHEELS, 2);
            createModelInstance(materialManager, ARCHBAR_FRAME);
        }

        @Override
        public BogeySizes.BogeySize getSize() {
            return BogeySizes.SMALL;
        }

        @Override
        public void render(CompoundTag bogeyData, float wheelAngle, PoseStack ms, int light, VertexConsumer vb, boolean inContraption) {
            boolean inInstancedContraption = vb == null;
            getTransform(ARCHBAR_FRAME, ms, inInstancedContraption)
                    .translate(0, 5 / 16f, 0)
                    .render(ms, light, vb);

            BogeyModelData[] wheels = getTransform(CR_BOGEY_WHEELS, ms, inInstancedContraption, 2);
            for (int side : Iterate.positiveAndNegative) {
                if (!inInstancedContraption)
                    ms.pushPose();
                BogeyModelData wheel = wheels[(side + 1) / 2];
                wheel.translate(0, 12 / 16f, side)
                        .rotateX(wheelAngle)
                        .translate(0, -7 / 16f, 0)
                        .render(ms, light, vb);
                if (!inInstancedContraption)
                    ms.popPose();
            }
        }
    }

    public static class PassengerBogeyRenderer extends BogeyRenderer {
        @Override
        public void initialiseContraptionModelData(MaterialManager materialManager, CarriageBogey carriageBogey) {
            createModelInstance(materialManager, CR_BOGEY_WHEELS, 2);
            createModelInstance(materialManager, PASSENGER_FRAME);
        }

        @Override
        public BogeySizes.BogeySize getSize() {
            return BogeySizes.SMALL;
        }

        @Override
        public void render(CompoundTag bogeyData, float wheelAngle, PoseStack ms, int light, VertexConsumer vb, boolean inContraption) {
            boolean inInstancedContraption = vb == null;
            getTransform(PASSENGER_FRAME, ms, inInstancedContraption)
                    .translate(0, 5 / 16f, 0)
                    .render(ms, light, vb);

            BogeyModelData[] wheels = getTransform(CR_BOGEY_WHEELS, ms, inInstancedContraption, 2);
            for (int side : Iterate.positiveAndNegative) {
                if (!inInstancedContraption)
                    ms.pushPose();
                BogeyModelData wheel = wheels[(side + 1) / 2];
                wheel.translate(0, 12 / 16f, side)
                        .rotateX(wheelAngle)
                        .translate(0, -7 / 16f, 0)
                        .render(ms, light, vb);
                if (!inInstancedContraption)
                    ms.popPose();
            }
        }
    }

    //TWOAXLESTWO

    public static class ModernBogeyRenderer extends BogeyRenderer {
        @Override
        public void initialiseContraptionModelData(MaterialManager materialManager, CarriageBogey carriageBogey) {
            createModelInstance(materialManager, CR_BOGEY_WHEELS, 2);
            createModelInstance(materialManager, MODERN_FRAME);
        }

        @Override
        public BogeySizes.BogeySize getSize() {
            return BogeySizes.SMALL;
        }

        @Override
        public void render(CompoundTag bogeyData, float wheelAngle, PoseStack ms, int light, VertexConsumer vb, boolean inContraption) {
            boolean inInstancedContraption = vb == null;
            getTransform(MODERN_FRAME, ms, inInstancedContraption)
                    .translate(0, 5 / 16f, 0)
                    .render(ms, light, vb);

            BogeyModelData[] wheels = getTransform(CR_BOGEY_WHEELS, ms, inInstancedContraption, 2);
            for (int side : Iterate.positiveAndNegative) {
                if (!inInstancedContraption)
                    ms.pushPose();
                BogeyModelData wheel = wheels[(side + 1) / 2];
                wheel.translate(0, 12 / 16f, side)
                        .rotateX(wheelAngle)
                        .translate(0, -7 / 16f, 0)
                        .render(ms, light, vb);
                if (!inInstancedContraption)
                    ms.popPose();
            }
        }
    }

    public static class BlombergBogeyRenderer extends BogeyRenderer {
        @Override
        public void initialiseContraptionModelData(MaterialManager materialManager, CarriageBogey carriageBogey) {
            createModelInstance(materialManager, CR_BOGEY_WHEELS, 2);
            createModelInstance(materialManager, BLOMBERG_FRAME);
        }

        @Override
        public BogeySizes.BogeySize getSize() {
            return BogeySizes.SMALL;
        }

        @Override
        public void render(CompoundTag bogeyData, float wheelAngle, PoseStack ms, int light, VertexConsumer vb, boolean inContraption) {
            boolean inInstancedContraption = vb == null;
            getTransform(BLOMBERG_FRAME, ms, inInstancedContraption)
                    .translate(0, 5 / 16f, 0)
                    .render(ms, light, vb);

            BogeyModelData[] wheels = getTransform(CR_BOGEY_WHEELS, ms, inInstancedContraption, 2);
            for (int side : Iterate.positiveAndNegative) {
                if (!inInstancedContraption)
                    ms.pushPose();
                BogeyModelData wheel = wheels[(side + 1) / 2];
                wheel.translate(0, 12 / 16f, side)
                        .rotateX(wheelAngle)
                        .translate(0, -7 / 16f, 0)
                        .render(ms, light, vb);
                if (!inInstancedContraption)
                    ms.popPose();
            }
        }
    }

    public static class Y25BogeyRenderer extends BogeyRenderer {
        @Override
        public void initialiseContraptionModelData(MaterialManager materialManager, CarriageBogey carriageBogey) {
            createModelInstance(materialManager, CR_BOGEY_WHEELS, 2);
            createModelInstance(materialManager, Y25_FRAME);
        }

        @Override
        public BogeySizes.BogeySize getSize() {
            return BogeySizes.SMALL;
        }

        @Override
        public void render(CompoundTag bogeyData, float wheelAngle, PoseStack ms, int light, VertexConsumer vb, boolean inContraption) {
            boolean inInstancedContraption = vb == null;
            getTransform(Y25_FRAME, ms, inInstancedContraption)
                    .translate(0, 5 / 16f, 0)
                    .render(ms, light, vb);

            BogeyModelData[] wheels = getTransform(CR_BOGEY_WHEELS, ms, inInstancedContraption, 2);
            for (int side : Iterate.positiveAndNegative) {
                if (!inInstancedContraption)
                    ms.pushPose();
                BogeyModelData wheel = wheels[(side + 1) / 2];
                wheel.translate(0, 12 / 16f, side)
                        .rotateX(wheelAngle)
                        .translate(0, -7 / 16f, 0)
                        .render(ms, light, vb);
                if (!inInstancedContraption)
                    ms.popPose();
            }
        }
    }

    //TRIPLEAXLES

    public static class HeavyweightBogeyRenderer extends BogeyRenderer {
        @Override
        public void initialiseContraptionModelData(MaterialManager materialManager, CarriageBogey carriageBogey) {
            createModelInstance(materialManager, CR_BOGEY_WHEELS, 3);
            createModelInstance(materialManager, HEAVYWEIGHT_FRAME);
        }

        @Override
        public BogeySizes.BogeySize getSize() {
            return BogeySizes.SMALL;
        }

        @Override
        public void render(CompoundTag bogeyData, float wheelAngle, PoseStack ms, int light, VertexConsumer vb, boolean inContraption) {
            boolean inInstancedContraption = vb == null;
            getTransform(HEAVYWEIGHT_FRAME, ms, inInstancedContraption)
                    .translate(0, 5 / 16f, 0)
                    .render(ms, light, vb);

            BogeyModelData[] wheels = getTransform(CR_BOGEY_WHEELS, ms, inInstancedContraption, 3);
            for (int side = -1; side < 2; side++) {
                if (!inInstancedContraption)
                    ms.pushPose();
                BogeyModelData wheel = wheels[side + 1];
                wheel.translate(0, 12 / 16f, side*1.5)
                        .rotateX(wheelAngle)
                        .translate(0, -7 / 16f, 0)
                        .render(ms, light, vb);
                if (!inInstancedContraption)
                    ms.popPose();
            }
        }
    }

    public static class RadialBogeyRenderer extends BogeyRenderer {
        @Override
        public void initialiseContraptionModelData(MaterialManager materialManager, CarriageBogey carriageBogey) {
            createModelInstance(materialManager, CR_BOGEY_WHEELS, 3);
            createModelInstance(materialManager, RADIAL_FRAME);
        }

        @Override
        public BogeySizes.BogeySize getSize() {
            return BogeySizes.SMALL;
        }

        @Override
        public void render(CompoundTag bogeyData, float wheelAngle, PoseStack ms, int light, VertexConsumer vb, boolean inContraption) {
            boolean inInstancedContraption = vb == null;
            getTransform(RADIAL_FRAME, ms, inInstancedContraption)
                    .translate(0, 5 / 16f, 0)
                    .render(ms, light, vb);

            BogeyModelData[] wheels = getTransform(CR_BOGEY_WHEELS, ms, inInstancedContraption, 3);
            for (int side = -1; side < 2; side++) {
                if (!inInstancedContraption)
                    ms.pushPose();
                BogeyModelData wheel = wheels[side + 1];
                wheel.translate(0, 12 / 16f, side*1.5)
                        .rotateX(wheelAngle)
                        .translate(0, -7 / 16f, 0)
                        .render(ms, light, vb);
                if (!inInstancedContraption)
                    ms.popPose();
            }
        }
    }

    //WIDEAXLESTWO

    public static class WideDefaultBogeyRenderer extends BogeyRenderer {
        @Override
        public void initialiseContraptionModelData(MaterialManager materialManager, CarriageBogey carriageBogey) {
            createModelInstance(materialManager, CR_WIDE_BOGEY_WHEELS, 2);
            createModelInstance(materialManager, WIDE_DEFAULT_FRAME);
            createModelInstance(materialManager, AllBlocks.SHAFT.getDefaultState()
                .setValue(ShaftBlock.AXIS, Direction.Axis.Z), 2);
        }

        @Override
        public BogeySizes.BogeySize getSize() {
            return BogeySizes.SMALL;
        }

        @Override
        public void render(CompoundTag bogeyData, float wheelAngle, PoseStack ms, int light, VertexConsumer vb, boolean inContraption) {
            boolean inInstancedContraption = vb == null;

            BogeyModelData[] secondaryShafts = getTransform(AllBlocks.SHAFT.getDefaultState()
                .setValue(ShaftBlock.AXIS, Direction.Axis.Z), ms, inInstancedContraption, 2);

            for (int i : Iterate.zeroAndOne) {
                secondaryShafts[i].translate(-.5, 6 / 16., .5 + i * -2)
                    .centre()
                    .rotateZ(wheelAngle)
                    .unCentre()
                    .render(ms, light, vb);
            }

            getTransform(WIDE_DEFAULT_FRAME, ms, inInstancedContraption)
                .translate(0, 5 / 16f, 0)
                .render(ms, light, vb);

            BogeyModelData[] wheels = getTransform(CR_WIDE_BOGEY_WHEELS, ms, inInstancedContraption, 2);
            for (int side : Iterate.positiveAndNegative) {
                if (!inInstancedContraption)
                    ms.pushPose();
                BogeyModelData wheel = wheels[(side + 1) / 2];
                wheel.translate(0, 14 / 16., side * 1.5)
                    .rotateX(wheelAngle)
                    .translate(0, 0, 0)
                    .render(ms, light, vb);
                if (!inInstancedContraption)
                    ms.popPose();
            }
        }
    }

    // Narrow

    public static class NarrowSmallBogeyRenderer extends BogeyRenderer {
        @Override
        public void initialiseContraptionModelData(MaterialManager materialManager, CarriageBogey carriageBogey) {
            createModelInstance(materialManager, NARROW_FRAME);
            createModelInstance(materialManager, NARROW_WHEELS, 2);
            createModelInstance(materialManager, AllPartialModels.SHAFT_HALF, 2);
        }

        @Override
        public BogeySizes.BogeySize getSize() {
            return BogeySizes.SMALL;
        }

        @Override
        public void render(CompoundTag bogeyData, float wheelAngle, PoseStack ms, int light, VertexConsumer vb, boolean inContraption) {
            boolean inInstancedContraption = vb == null;

            BogeyModelData[] secondaryShafts = getTransform(AllPartialModels.SHAFT_HALF, ms, inInstancedContraption, 2);

            for (int i : Iterate.zeroAndOne) {
                secondaryShafts[i].translate(-.5, 1 / 16., -(18 / 16.) + (i * 12 / 16.))
                    .centre()
                    .rotateZ(wheelAngle)
                    .unCentre()
                    .render(ms, light, vb);
            }

            getTransform(NARROW_FRAME, ms, inInstancedContraption)
                .translate(0, 5 / 16f, 0)
                .render(ms, light, vb);

            BogeyModelData[] wheels = getTransform(NARROW_WHEELS, ms, inInstancedContraption, 2);
            for (int side : Iterate.positiveAndNegative) {
                if (!inInstancedContraption)
                    ms.pushPose();
                BogeyModelData wheel = wheels[(side + 1) / 2];
                wheel.translate(0, 11 / 16., side * (10 / 16.))
                    .rotateX(wheelAngle)
                    .translate(0, 0, 0)
                    .render(ms, light, vb);
                if (!inInstancedContraption)
                    ms.popPose();
            }
        }
    }

    public static class NarrowScotchYokeBogeyRenderer extends BogeyRenderer {
        @Override
        public void initialiseContraptionModelData(MaterialManager materialManager, CarriageBogey carriageBogey) {
            createModelInstance(materialManager, NARROW_SCOTCH_FRAME, NARROW_SCOTCH_WHEELS,
                NARROW_SCOTCH_WHEEL_PINS, NARROW_SCOTCH_PISTONS);
            createModelInstance(materialManager, AllBlocks.SHAFT.getDefaultState()
                .setValue(ShaftBlock.AXIS, Direction.Axis.Z), 2);
            createModelInstance(materialManager, AllBlocks.SHAFT.getDefaultState()
                .setValue(ShaftBlock.AXIS, Direction.Axis.X), 2);
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
                primaryShafts[i].translate(-.5, 1 / 16., i * -1)
                    .centre()
                    .rotateZ(wheelAngle)
                    .unCentre()
                    .render(ms, light, vb);
            }

            BogeyModelData[] secondaryShafts = getTransform(AllBlocks.SHAFT.getDefaultState()
                .setValue(ShaftBlock.AXIS, Direction.Axis.X), ms, inInstancedContraption, 2);

            for (int i : Iterate.zeroAndOne) {
                secondaryShafts[i]
                    .translate(-.5f, 6 / 16., (6 / 16.) + i * -(28 / 16.))
                    .centre()
                    .rotateX(wheelAngle)
                    .unCentre()
                    .render(ms, light, vb);
            }

            getTransform(NARROW_SCOTCH_FRAME, ms, inInstancedContraption)
                .translate(0, 5 / 16f, 0)
                .render(ms, light, vb);

            getTransform(NARROW_SCOTCH_PISTONS, ms, inInstancedContraption)
                .translate(0, 5 / 16f, 1 / 4f * Math.sin(AngleHelper.rad(wheelAngle)))
                .render(ms, light, vb);

            if (!inInstancedContraption)
                ms.pushPose();

            getTransform(NARROW_SCOTCH_WHEELS, ms, inInstancedContraption)
                .translate(0, 14 / 16., 0)// 14/16
                .rotateX(wheelAngle)
                .translate(0, 0, 0)
                .render(ms, light, vb);

            getTransform(NARROW_SCOTCH_WHEEL_PINS, ms, inInstancedContraption)
                .translate(0, 14 / 16., 0)
                .rotateX(wheelAngle)
                .translate(0, 1 / 4f, 0)
                .rotateX(-wheelAngle)
                .render(ms, light, vb);

            if (!inInstancedContraption)
                ms.popPose();
        }
    }

    public static class NarrowDoubleScotchYokeBogeyRenderer extends BogeyRenderer {
        @Override
        public void initialiseContraptionModelData(MaterialManager materialManager, CarriageBogey carriageBogey) {
            createModelInstance(materialManager, NARROW_DOUBLE_SCOTCH_FRAME, NARROW_DOUBLE_SCOTCH_PISTONS);
            createModelInstance(materialManager, NARROW_SCOTCH_WHEELS, 2);
            createModelInstance(materialManager, NARROW_SCOTCH_WHEEL_PINS, 2);
            createModelInstance(materialManager, AllBlocks.SHAFT.getDefaultState()
                .setValue(ShaftBlock.AXIS, Direction.Axis.Z), 2);
            createModelInstance(materialManager, AllBlocks.SHAFT.getDefaultState()
                .setValue(ShaftBlock.AXIS, Direction.Axis.X), 2);
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
                primaryShafts[i].translate(-.5, 1 / 16., (7/16.) + i * -(30 / 16.))
                    .centre()
                    .rotateZ(wheelAngle)
                    .unCentre()
                    .render(ms, light, vb);
            }

            BogeyModelData[] secondaryShafts = getTransform(AllBlocks.SHAFT.getDefaultState()
                .setValue(ShaftBlock.AXIS, Direction.Axis.X), ms, inInstancedContraption, 2);

            for (int i : Iterate.zeroAndOne) {
                secondaryShafts[i]
                    .translate(-.5f, 6 / 16., (18 / 16.) + i * -(52 / 16.))
                    .centre()
                    .rotateX(wheelAngle)
                    .unCentre()
                    .render(ms, light, vb);
            }

            getTransform(NARROW_DOUBLE_SCOTCH_FRAME, ms, inInstancedContraption)
                .translate(0, 5 / 16f, 0)
                .render(ms, light, vb);

            getTransform(NARROW_DOUBLE_SCOTCH_PISTONS, ms, inInstancedContraption)
                .translate(0, 14 / 16f, 1 / 4f * Math.sin(AngleHelper.rad(wheelAngle)))
                .render(ms, light, vb);

            BogeyModelData[] wheels = getTransform(NARROW_SCOTCH_WHEELS, ms, inInstancedContraption, 2);
            BogeyModelData[] pins = getTransform(NARROW_SCOTCH_WHEEL_PINS, ms, inInstancedContraption, 2);

            for (int side : Iterate.positiveAndNegative) {
                if (!inInstancedContraption)
                    ms.pushPose();

                wheels[(side + 1) / 2]
                    .translate(0, 14 / 16., side * (12 / 16.))
                    .rotateX(wheelAngle)
                    .translate(0, 0, 0)
                    .render(ms, light, vb);

                pins[(side + 1) / 2]
                    .translate(0, 14 / 16., side * (12 / 16.))
                    .rotateX(wheelAngle)
                    .translate(0, 1 / 4f, 0)
                    .rotateX(-wheelAngle)
                    .render(ms, light, vb);

                if (!inInstancedContraption)
                    ms.popPose();
            }
        }
    }
}
