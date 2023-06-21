package com.railwayteam.railways.content.custom_bogeys;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.util.transform.Transform;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.railwayteam.railways.registry.CRBogeySizes;
import com.simibubi.create.AllBogeyStyles;
import com.simibubi.create.content.trains.bogey.BogeyRenderer;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.foundation.utility.Iterate;
import net.minecraft.nbt.CompoundTag;

import static com.railwayteam.railways.registry.CRBlockPartials.*;
import static com.simibubi.create.AllPartialModels.SMALL_BOGEY_WHEELS;

public class CRBogeyRenderer {

    //SINGLEAXLES:

    public static class SingleaxleBogeyRenderer extends BogeyRenderer {
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
            Transform<?> transform = getTransformFromPartial(SINGLEAXLE_FRAME, ms, inInstancedContraption)
                    .translate(0, 5 / 16f, 0);
            finalize(transform, ms, light, vb);

            Transform<?> bogeyWheels = getTransformFromPartial(SMALL_BOGEY_WHEELS, ms, inInstancedContraption)
                    .translate(0, 12 / 16f, 0)
                    .rotateX(wheelAngle);
            finalize(bogeyWheels, ms, light, vb);
        }
    }

    public static class LeafspringBogeyRenderer extends BogeyRenderer {
        @Override
        public void initialiseContraptionModelData(MaterialManager materialManager) {
            createModelInstances(materialManager, CR_BOGEY_WHEELS, 1);
            createModelInstances(materialManager, LEAFSPRING_FRAME);
        }

        @Override
        public BogeySizes.BogeySize getSize() {
            return BogeySizes.LARGE;
        }

        @Override
        public void render(CompoundTag bogeyData, float wheelAngle, PoseStack ms, int light, VertexConsumer vb, boolean inContraption) {
            boolean inInstancedContraption = vb == null;
            Transform<?> transform = getTransformFromPartial(LEAFSPRING_FRAME, ms, inInstancedContraption)
                    .translate(0, 5 / 16f, 0);
            finalize(transform, ms, light, vb);

            Transform<?> bogeyWheels = getTransformFromPartial(CR_BOGEY_WHEELS, ms, inInstancedContraption)
                    .translate(0, 5 / 16f, 0)
                    .rotateX(wheelAngle);
            finalize(bogeyWheels, ms, light, vb);
        }
    }

    public static class CoilspringBogeyRenderer extends BogeyRenderer {
        @Override
        public void initialiseContraptionModelData(MaterialManager materialManager) {
            createModelInstances(materialManager, CR_BOGEY_WHEELS, 1);
            createModelInstances(materialManager, COILSPRING_FRAME);
        }

        @Override
        public BogeySizes.BogeySize getSize() {
            return CRBogeySizes.EXTRA;
        }

        @Override
        public void render(CompoundTag bogeyData, float wheelAngle, PoseStack ms, int light, VertexConsumer vb, boolean inContraption) {
            boolean inInstancedContraption = vb == null;
            Transform<?> transform = getTransformFromPartial(COILSPRING_FRAME, ms, inInstancedContraption)
                    .translate(0, 5 / 16f, 0);
            finalize(transform, ms, light, vb);

            Transform<?> bogeyWheels = getTransformFromPartial(CR_BOGEY_WHEELS, ms, inInstancedContraption)
                    .translate(0, 5 / 16f, 0)
                    .rotateX(wheelAngle);
            finalize(bogeyWheels, ms, light, vb);
        }
    }

    //TWOAXLESONE:

    public static class FreightBogeyRenderer extends BogeyRenderer {
        @Override
        public void initialiseContraptionModelData(MaterialManager materialManager) {
            createModelInstances(materialManager, CR_BOGEY_WHEELS, 2);
            createModelInstances(materialManager, FREIGHT_FRAME);
        }

        @Override
        public BogeySizes.BogeySize getSize() {
            return BogeySizes.SMALL;
        }

        @Override
        public void render(CompoundTag bogeyData, float wheelAngle, PoseStack ms, int light, VertexConsumer vb, boolean inContraption) {
            boolean inInstancedContraption = vb == null;
            Transform<?> transform = getTransformFromPartial(FREIGHT_FRAME, ms, inInstancedContraption)
                    .translate(0, 5 / 16f, 0);
            finalize(transform, ms, light, vb);

            Transform<?>[] wheels = getTransformsFromPartial(CR_BOGEY_WHEELS, ms, inInstancedContraption, 2);
            for (int side : Iterate.positiveAndNegative) {
                if (!inInstancedContraption)
                    ms.pushPose();
                Transform<?> wheel = wheels[(side + 1) / 2];
                wheel.translate(0, 5 / 16f, side)
                        .rotateX(wheelAngle);
                finalize(wheel, ms, light, vb);
                if (!inInstancedContraption)
                    ms.popPose();
            }
        }
    }

    public static class ArchbarBogeyRenderer extends BogeyRenderer {
        @Override
        public void initialiseContraptionModelData(MaterialManager materialManager) {
            createModelInstances(materialManager, CR_BOGEY_WHEELS, 2);
            createModelInstances(materialManager, ARCHBAR_FRAME);
        }

        @Override
        public BogeySizes.BogeySize getSize() {
            return BogeySizes.LARGE;
        }

        @Override
        public void render(CompoundTag bogeyData, float wheelAngle, PoseStack ms, int light, VertexConsumer vb, boolean inContraption) {
            boolean inInstancedContraption = vb == null;
            Transform<?> transform = getTransformFromPartial(ARCHBAR_FRAME, ms, inInstancedContraption)
                    .translate(0, 5 / 16f, 0);
            finalize(transform, ms, light, vb);

            Transform<?>[] wheels = getTransformsFromPartial(CR_BOGEY_WHEELS, ms, inInstancedContraption, 2);
            for (int side : Iterate.positiveAndNegative) {
                if (!inInstancedContraption)
                    ms.pushPose();
                Transform<?> wheel = wheels[(side + 1) / 2];
                wheel.translate(0, 5 / 16f, side)
                        .rotateX(wheelAngle);
                finalize(wheel, ms, light, vb);
                if (!inInstancedContraption)
                    ms.popPose();
            }
        }
    }

    public static class PassengerBogeyRenderer extends BogeyRenderer {
        @Override
        public void initialiseContraptionModelData(MaterialManager materialManager) {
            createModelInstances(materialManager, CR_BOGEY_WHEELS, 2);
            createModelInstances(materialManager, PASSENGER_FRAME);
        }

        @Override
        public BogeySizes.BogeySize getSize() {
            return CRBogeySizes.EXTRA;
        }

        @Override
        public void render(CompoundTag bogeyData, float wheelAngle, PoseStack ms, int light, VertexConsumer vb, boolean inContraption) {
            boolean inInstancedContraption = vb == null;
            Transform<?> transform = getTransformFromPartial(PASSENGER_FRAME, ms, inInstancedContraption)
                    .translate(0, 5 / 16f, 0);
            finalize(transform, ms, light, vb);

            Transform<?>[] wheels = getTransformsFromPartial(CR_BOGEY_WHEELS, ms, inInstancedContraption, 2);
            for (int side : Iterate.positiveAndNegative) {
                if (!inInstancedContraption)
                    ms.pushPose();
                Transform<?> wheel = wheels[(side + 1) / 2];
                wheel.translate(0, 5 / 16f, side)
                        .rotateX(wheelAngle);
                finalize(wheel, ms, light, vb);
                if (!inInstancedContraption)
                    ms.popPose();
            }
        }
    }

    //TWOAXLESTWO

    public static class ModernBogeyRenderer extends BogeyRenderer {
        @Override
        public void initialiseContraptionModelData(MaterialManager materialManager) {
            createModelInstances(materialManager, CR_BOGEY_WHEELS, 2);
            createModelInstances(materialManager, MODERN_FRAME);
        }

        @Override
        public BogeySizes.BogeySize getSize() {
            return CRBogeySizes.EXTRA;
        }

        @Override
        public void render(CompoundTag bogeyData, float wheelAngle, PoseStack ms, int light, VertexConsumer vb, boolean inContraption) {
            boolean inInstancedContraption = vb == null;
            Transform<?> transform = getTransformFromPartial(MODERN_FRAME, ms, inInstancedContraption)
                    .translate(0, 5 / 16f, 0);
            finalize(transform, ms, light, vb);

            Transform<?>[] wheels = getTransformsFromPartial(CR_BOGEY_WHEELS, ms, inInstancedContraption, 2);
            for (int side : Iterate.positiveAndNegative) {
                if (!inInstancedContraption)
                    ms.pushPose();
                Transform<?> wheel = wheels[(side + 1) / 2];
                wheel.translate(0, 5 / 16f, side)
                        .rotateX(wheelAngle);
                finalize(wheel, ms, light, vb);
                if (!inInstancedContraption)
                    ms.popPose();
            }
        }
    }

    public static class BloombergBogeyRenderer extends BogeyRenderer {
        @Override
        public void initialiseContraptionModelData(MaterialManager materialManager) {
            createModelInstances(materialManager, CR_BOGEY_WHEELS, 2);
            createModelInstances(materialManager, BLOMBERG_FRAME);
        }

        @Override
        public BogeySizes.BogeySize getSize() {
            return CRBogeySizes.EXTRA;
        }

        @Override
        public void render(CompoundTag bogeyData, float wheelAngle, PoseStack ms, int light, VertexConsumer vb, boolean inContraption) {
            boolean inInstancedContraption = vb == null;
            Transform<?> transform = getTransformFromPartial(BLOMBERG_FRAME, ms, inInstancedContraption)
                    .translate(0, 5 / 16f, 0);
            finalize(transform, ms, light, vb);

            Transform<?>[] wheels = getTransformsFromPartial(CR_BOGEY_WHEELS, ms, inInstancedContraption, 2);
            for (int side : Iterate.positiveAndNegative) {
                if (!inInstancedContraption)
                    ms.pushPose();
                Transform<?> wheel = wheels[(side + 1) / 2];
                wheel.translate(0, 5 / 16f, side)
                        .rotateX(wheelAngle);
                finalize(wheel, ms, light, vb);
                if (!inInstancedContraption)
                    ms.popPose();
            }
        }
    }

    public static class Y25BogeyRenderer extends BogeyRenderer {
        @Override
        public void initialiseContraptionModelData(MaterialManager materialManager) {
            createModelInstances(materialManager, CR_BOGEY_WHEELS, 2);
            createModelInstances(materialManager, Y25_FRAME);
        }

        @Override
        public BogeySizes.BogeySize getSize() {
            return CRBogeySizes.EXTRA;
        }

        @Override
        public void render(CompoundTag bogeyData, float wheelAngle, PoseStack ms, int light, VertexConsumer vb, boolean inContraption) {
            boolean inInstancedContraption = vb == null;
            Transform<?> transform = getTransformFromPartial(Y25_FRAME, ms, inInstancedContraption)
                    .translate(0, 5 / 16f, 0);
            finalize(transform, ms, light, vb);

            Transform<?>[] wheels = getTransformsFromPartial(CR_BOGEY_WHEELS, ms, inInstancedContraption, 2);
            for (int side : Iterate.positiveAndNegative) {
                if (!inInstancedContraption)
                    ms.pushPose();
                Transform<?> wheel = wheels[(side + 1) / 2];
                wheel.translate(0, 5 / 16f, side)
                        .rotateX(wheelAngle);
                finalize(wheel, ms, light, vb);
                if (!inInstancedContraption)
                    ms.popPose();
            }
        }
    }

}
