package com.railwayteam.railways.content.smokestack;


import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.railwayteam.railways.registry.CRBlockPartials;
import com.simibubi.create.content.contraptions.behaviour.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.content.contraptions.render.ContraptionRenderDispatcher;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageContraption;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.utility.*;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

import static com.railwayteam.railways.util.Utils.getBounds;

public class SmokeStackMovementBehaviour implements MovementBehaviour {

    private final boolean renderAsNormalBlockEntity;
    private final boolean createsSmoke;
    private final boolean spawnExtraSmoke;

    private final Map<Integer, LerpedFloat> chanceChasers = new HashMap<>();
    private final Map<Integer, LerpedFloat> speedMultiplierChasers = new HashMap<>();

    public SmokeStackMovementBehaviour() {
        this(true);
    }

    public SmokeStackMovementBehaviour(boolean spawnExtraSmoke) {
        this(false, true, spawnExtraSmoke);
    }

    public SmokeStackMovementBehaviour(boolean renderAsNormalBlockEntity, boolean createsSmoke, boolean spawnExtraSmoke) {
        this.renderAsNormalBlockEntity = renderAsNormalBlockEntity;
        this.createsSmoke = createsSmoke;
        this.spawnExtraSmoke = spawnExtraSmoke;
    }

    @Override
    public boolean renderAsNormalBlockEntity() {
        return renderAsNormalBlockEntity;
    }

    @Override
    public void tick(MovementContext context) {
        if (context.world == null || !context.world.isClientSide || context.position == null
            || !context.state.getValue(SmokeStackBlock.ENABLED))
            return;

        int key = context.hashCode();

        LerpedFloat chanceChaser = chanceChasers.get(key);
        LerpedFloat speedMultiplierChaser = speedMultiplierChasers.get(key);

        if (chanceChaser == null) {
            chanceChaser = LerpedFloat.linear();
            chanceChasers.put(key, chanceChaser);
        }
        if (speedMultiplierChaser == null) {
            speedMultiplierChaser = LerpedFloat.linear();
            speedMultiplierChasers.put(key, speedMultiplierChaser);
        }

        float chanceModifierTarget = (Math.abs(context.getAnimationSpeed()) + 100) / 800;
        chanceModifierTarget = chanceModifierTarget * chanceModifierTarget;

        if (context.contraption.presentBlockEntities.get(context.localPos) instanceof ISpeedNotifiable notifiable) {
            notifiable.notifySpeed(chanceModifierTarget);
        }

/*        Carriage carriage;
        if (context.contraption.entity instanceof CarriageContraptionEntity cce && (carriage = cce.getCarriage()) != null) {
            Train train = carriage.train;
            double actualSpeed = train.speed;
            chanceModifierTarget = (float) ((Math.abs(actualSpeed * 1500) + 100) / 800);
            chanceModifierTarget = chanceModifierTarget * chanceModifierTarget;
        }*/

        if (!createsSmoke)
            return;

        chanceChaser.chase(chanceModifierTarget, chanceModifierTarget>chanceChaser.getChaseTarget() ? 0.1 : 0.01, LerpedFloat.Chaser.LINEAR);
        chanceChaser.tickChaser();
        float chanceModifier = chanceChaser.getValue() * (spawnExtraSmoke ? 1.0f : 0.5f);

        int maxModifier = 0;
        int minModifier = 0;
        if (chanceModifier > 2) {
            maxModifier += (int) (chanceModifier+0.5) - 1;
            if (chanceModifier > 3) {
                minModifier = (int) (chanceModifier+0.5) - 2;
            } else {
                minModifier = 1;
            }
        } else if (chanceModifier > 1) {
            maxModifier++;
        }

        minModifier += 5;
        maxModifier += 15;

        // Mostly copied from CampfireBlock and CampfireBlockEntity
        RandomSource random = context.world.random;
        SmokeStackBlock.SmokeStackType type = ((SmokeStackBlock) context.state.getBlock()).type;
        double speedModifierTarget = 5 * (0.5+maxModifier);
        speedMultiplierChaser.chase(speedModifierTarget, 0.4, LerpedFloat.Chaser.LINEAR);
        speedMultiplierChaser.tickChaser();
        if (random.nextFloat() < type.particleSpawnChance * chanceModifier) {
            for(int i = 0; i < random.nextInt((type.maxParticles + maxModifier - (type.minParticles + minModifier))) + type.minParticles + minModifier; ++i) {
                /*context.world.addAlwaysVisibleParticle(
                    context.state.getValue(CampfireBlock.SIGNAL_FIRE) ? ParticleTypes.CAMPFIRE_SIGNAL_SMOKE
                        : ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    true, context.position.x() + random.nextDouble() / (random.nextBoolean() ? 3D : -3D),
                    context.position.y() + random.nextDouble() + random.nextDouble(),
                    context.position.z() + random.nextDouble() / (random.nextBoolean() ? 3D : -3D), 0.0D, 0.07D,
                    0.0D);*/
                BlockState underState = Blocks.AIR.defaultBlockState();
                StructureBlockInfo info;
                if ((info = context.contraption.getBlocks().get(context.localPos.below())) != null)
                    underState = info.state;
                SmokeStackBlock.makeParticles(context.world, context.position.subtract(0.5, 0, 0.5).subtract((random.nextDouble()-0.5)*0.5, (random.nextDouble()-0.5)*0.5, (random.nextDouble()-0.5)*0.5), random.nextBoolean(), true,
                    type.getParticleSpawnOffset(), type.getParticleSpawnDelta(), speedMultiplierChaser.getValue(), false, underState);
            }
        }
    }

    private Vec3 lerpVec3(float progress, Vec3 a, Vec3 b) {
        return new Vec3(
            Mth.lerp(progress, a.x, b.x),
            Mth.lerp(progress, a.y, b.y),
            Mth.lerp(progress, a.z, b.z)
        );
    }

    @Override
    public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
        if (!(context.contraption instanceof CarriageContraption cc))
            return;

        BlockPos localPos = context.localPos;

        Couple<BlockPos> blockBounds = getBounds(cc.anchor, cc.getBlocks().keySet());
        BlockPos minPos = blockBounds.getFirst();
        BlockPos maxPos = blockBounds.getSecond();

        boolean front;

        switch (cc.getAssemblyDirection().getAxis()) {
            case X -> {
                if (localPos.getX() != minPos.getX() && localPos.getX() != maxPos.getX())
                    return;
                front = (cc.getAssemblyDirection().getAxisDirection() == AxisDirection.POSITIVE) ? (localPos.getX() == minPos.getX()) : (localPos.getX() == maxPos.getX());
            }
            case Z -> {
                if (localPos.getZ() != minPos.getZ() && localPos.getZ() != maxPos.getZ())
                    return;
                front = (cc.getAssemblyDirection().getAxisDirection() == AxisDirection.POSITIVE) ? (localPos.getZ() == minPos.getZ()) : (localPos.getZ() == maxPos.getZ());
            }
            default -> {
                return;
            }
        }

        CarriageContraptionEntity cce = (CarriageContraptionEntity) cc.entity;
        int carriageIndex = cce.carriageIndex;
        if (front && carriageIndex == 0) return;
        Train train = cce.getCarriage().train;
        int lastIndex = train.carriages.size() - 1;
        if (!front && carriageIndex == lastIndex) return;

        int adjacentCarriageIdx = front ? carriageIndex - 1 : carriageIndex + 1;
        Carriage adjacentCarriage = train.carriages.get(adjacentCarriageIdx);
        CarriageContraptionEntity adjacentCCE = adjacentCarriage.getDimensional(renderWorld.dimension()).entity.get();
        if (adjacentCCE == null)
            return;

        CarriageContraption adjacentCC = (CarriageContraption) adjacentCCE.getContraption();

        Couple<BlockPos> adjacentBounds = getBounds(adjacentCC.anchor, adjacentCC.getBlocks().keySet());
        BlockPos adjMinPos = adjacentBounds.getFirst();
        BlockPos adjMaxPos = adjacentBounds.getSecond();

        BlockPos neighborPos;

        if (adjacentCC.getAssemblyDirection().getAxis() == Axis.X) {
            int otherX = adjacentCC.getAssemblyDirection().getAxisDirection() == AxisDirection.POSITIVE ^ front ? adjMinPos.getX() : adjMaxPos.getX();
            neighborPos = new BlockPos(otherX, localPos.getY(), localPos.getZ());
        } else {
            int otherZ = adjacentCC.getAssemblyDirection().getAxisDirection() == AxisDirection.POSITIVE ^ front ? adjMinPos.getZ() : adjMaxPos.getZ();
            neighborPos = new BlockPos(localPos.getX(), localPos.getY(), otherZ);
        }

        StructureBlockInfo neighborInfo = adjacentCC.getBlocks().get(neighborPos);
        if (neighborInfo == null)
            return;

        if (!(neighborInfo.state.getBlock() instanceof DieselSmokeStackBlock))
            return;

        PoseStack ms = matrices.getModel();
        ms.pushPose();
        float angleDiff = cce.yaw - adjacentCCE.yaw;

        float partialTicks = AnimationTickHolder.getPartialTicks(renderWorld);

        // have to lerp between previous and current anchor because `toGlobalVector` doesn't already do that for some reason
        Vec3 myGlobalPos = lerpVec3(partialTicks,
            cce.toGlobalVector(Vec3.atCenterOf(localPos), partialTicks, true),
            cce.toGlobalVector(Vec3.atCenterOf(localPos), partialTicks, false));
        Vec3 neighborGlobalPos = lerpVec3(partialTicks,
            adjacentCCE.toGlobalVector(Vec3.atCenterOf(neighborPos), partialTicks, true),
            adjacentCCE.toGlobalVector(Vec3.atCenterOf(neighborPos), partialTicks, false));

        int height = 1;
        Vec3 myGlobalTopPos = lerpVec3(partialTicks,
            cce.toGlobalVector(Vec3.atCenterOf(localPos.above(height)), partialTicks, true),
            cce.toGlobalVector(Vec3.atCenterOf(localPos.above(height)), partialTicks, false));
        Vec3 neighborGlobalTopPos = lerpVec3(partialTicks,
            adjacentCCE.toGlobalVector(Vec3.atCenterOf(neighborPos.above(height)), partialTicks, true),
            adjacentCCE.toGlobalVector(Vec3.atCenterOf(neighborPos.above(height)), partialTicks, false));

        float angleInitialYaw = cce.getInitialYaw();
        float angleYaw = cce.getViewYRot(partialTicks);
        float anglePitch = cce.getViewXRot(partialTicks);
        TransformStack.cast(ms)
            //.rotateY(cce.yaw)
            .translate(localPos.multiply(-1))
            .centre()
            .rotateY(-angleInitialYaw)
            .rotateZ(-anglePitch)
            .rotateY(-angleYaw)
            .unCentre()
            .translate(myGlobalPos.subtract(cce.getPosition(partialTicks)))
            ;//.translate(localPos);
//        ms.mulPose(Vector3f.YP.rotationDegrees(cce.yaw+90));
        /*TransformStack.cast(ms)
            .translate(localPos);*/

        double centerGap = 1 / 16.;
        Vec3 realCenterOffset = neighborGlobalPos.subtract(myGlobalPos).scale(0.5);
        Vec3 centerOffset = realCenterOffset.subtract(realCenterOffset.normalize().scale(centerGap));
        Vec3 centerTopOffset = neighborGlobalTopPos.subtract(myGlobalTopPos).scale(0.5);
        centerTopOffset = centerTopOffset.subtract(centerTopOffset.normalize().scale(centerGap));
        double offsetToCenter = centerOffset.length();//train.carriageSpacing.get(front ? carriageIndex - 1 : carriageIndex) / 4.;
        double offsetToTopCenter = centerTopOffset.length();

        double diffX1 = centerOffset.x;
        double diffZ1 = centerOffset.z;

        float yRot = AngleHelper.deg(Mth.atan2(diffZ1, diffX1)) + 90;


        Vec3 diff = (centerTopOffset.add(myGlobalTopPos)).subtract((centerOffset.add(myGlobalPos))).normalize();//centerOffset.subtract(centerTopOffset);

        double diffX2 = diff.x;
        double diffY2 = diff.y;
        double diffZ2 = diff.z;

        float xRot = AngleHelper.deg(Math.atan2(diffY2, Math.sqrt(diffX2 * diffX2 + diffZ2 * diffZ2)));

        //Railways.LOGGER.info("diff: "+diff+", xRot: "+xRot);

//        Railways.LOGGER.info("front: "+front+" yRot: "+yRot);

        if (front) {
            CachedBufferer.partial(CRBlockPartials.GANGWAY_CENTER_CONNECTOR, Blocks.AIR.defaultBlockState())
                .transform(ms)
                .translate(centerOffset)//.subtract(centerOffset.normalize().scale(7 / 16.)))
//            .rotateZ(90)
//            .translateY(-0.5)
///            .translateY(12 / 16.)
//            .translateY(-0.5)
                .centre()
                .rotateY(-yRot)//(AngleHelper.deg(Mth.atan2(neighborGlobalPos.subtract(myGlobalPos).normalize().z, neighborGlobalPos.subtract(myGlobalPos).normalize().x))+90))
                .unCentre()
                .translateZ(7 / 16.)  // front switch not working, look into yRot as an answer
                .rotateX(xRot-90)// * (centerTopOffset.y > 0 ? -1 : 1))// + ((AnimationTickHolder.getRenderTime() % 60)*.5))//-90)      // must do centering *after* rotation
//            .translateY(12 / 16.)
//            .translateY(-8 / 16.)
                .translateZ(-12 / 16.)
//                .translateY( -4 / 16.)
                .light(matrices.getWorld(), ContraptionRenderDispatcher.getContraptionWorldLight(context, renderWorld))
                .renderInto(matrices.getViewProjection(), buffer.getBuffer(RenderType.cutoutMipped()));
        }

        /*for (double offset = 0.0; offset < offsetToCenter; offset += 12 / 16f) {
            Vec3 intermediateOffset = centerOffset.scale(offset/offsetToCenter);
            CachedBufferer.partial(AllPartialModels.PECULIAR_BELL, Blocks.AIR.defaultBlockState())
                .transform(ms)
                .translate(intermediateOffset)
                //.centre()
                //.rotateY(AngleHelper.horizontalAngle(facing))
                //.rotateX(AngleHelper.verticalAngle(facing))
                //.rotateZ(angle)
                //.unCentre()
                .light(matrices.getWorld(),
                    ContraptionRenderDispatcher.getContraptionWorldLight(context, renderWorld))
                .renderInto(matrices.getViewProjection(), buffer.getBuffer(RenderType.cutoutMipped()));
        }*/

        //*****************************
        //* Start Line Rendering Test *
        //*****************************
        ms.pushPose();

        PoseStack ms2 = matrices.getViewProjection();
        ms2.pushPose();

        ms2.mulPoseMatrix(ms.last().pose());

        //VertexConsumer vc = buffer.getBuffer(RenderType.lineStrip());
        Pose pose = ms2.last();

        /*float h = 1.0f;
        vc.vertex(pose.pose(), 0.5f, 0.5f, 0.5f).color(1.0f, 0.0f, 0.0f, 1.0f).normal(pose.normal(), 1.0f, 1.0f, 0.0f).endVertex();
        vc.vertex(pose.pose(), (float) centerOffset.x+0.5f, (float) centerOffset.y+0.5f, (float) centerOffset.z+0.5f).color(1.0f, 0.0f, 0.0f, 1.0f).normal(pose.normal(),1.0f, 1.0f, 0.0f).endVertex();

        vc.vertex(pose.pose(), (float) centerOffset.x+0.5f, (float) centerOffset.y+0.5f+h, (float) centerOffset.z+0.5f).color(0.0f, 1.0f, 0.0f, 1.0f).normal(pose.normal(),1.0f, 1.0f, 0.0f).endVertex();
        vc.vertex(pose.pose(), 0.5f, 0.5f+h, 0.5f).color(0.0f, 0.0f, 1.0f, 1.0f).normal(pose.normal(), 1.0f, 1.0f, 0.0f).endVertex();
        vc.vertex(pose.pose(), 0.5f, 0.5f, 0.5f).color(1.0f, 0.0f, 0.0f, 1.0f).normal(pose.normal(), 1.0f, 1.0f, 0.0f).endVertex();
*/
        /* staggered sideways bellows
        <
         >
        <
         >
        <
         >

        bellow count is number of v's
        straightLength is distance to center offset / bellowCount
        flapLength is a constant length for a flap
         */

        //VertexConsumer bellowConsumer = buffer.getBuffer(RenderType.lines());
        double flapLength = 8 / 16.;

        for (boolean top : Iterate.falseAndTrue) {
            VertexConsumer vc = buffer.getBuffer(RenderType.lineStrip());

            int bellowCount = train.carriageSpacing.get(front ? carriageIndex - 1 : carriageIndex);
            double straightLength = (top ? offsetToTopCenter : offsetToCenter) / bellowCount;
            double halfStraightLength = straightLength / 2;

            double coveredLength = 0.0;

            double flapSideways = Math.sqrt((flapLength*flapLength) - (halfStraightLength*halfStraightLength));
            if (Double.isNaN(flapSideways)) {
                flapSideways = 0.0;
            }


            Vec3 baseOffset = top ? myGlobalTopPos.subtract(myGlobalPos) : Vec3.ZERO;
            Vec3 normalizedForward = (top ? centerTopOffset : centerOffset).normalize();
            Vec3 normalizedRight = VecHelper.rotate(new Vec3(normalizedForward.x, 0, normalizedForward.z), 90, Axis.Y);
            normalizedRight = new Vec3(normalizedRight.x, 0, normalizedRight.z).normalize();

            for (int i = 0; i < bellowCount; i++) {
                int sideScale = i % 2 == 0 ? -1 : 1;
                Vec3 firstCoordinate = normalizedForward.scale(coveredLength).add(0.5, 0.0, 0.5).add(baseOffset);
                Vec3 sideCoordinate = firstCoordinate.add(normalizedForward.scale(halfStraightLength))
                    .add(normalizedRight.scale(flapSideways * sideScale));
                Vec3 secondCoordinate = firstCoordinate.add(normalizedForward.scale(straightLength));
                coveredLength += straightLength;

                int firstColor = i % 2 == 0 ? 0xFFFF0000 : 0xFFFFFF00;
                int secondColor = i % 2 == 0 ? 0xFF0000FF : 0xFF00FF00;

                // first line
                vc.vertex(pose.pose(), (float) firstCoordinate.x, (float) firstCoordinate.y, (float) firstCoordinate.z).color(firstColor).normal(pose.normal(), 0.0f, 1.0f, 0.0f).endVertex();
                vc.vertex(pose.pose(), (float) sideCoordinate.x, (float) sideCoordinate.y, (float) sideCoordinate.z).color(firstColor).normal(pose.normal(), 0.0f, 1.0f, 0.0f).endVertex();

                // second line
                vc.vertex(pose.pose(), (float) sideCoordinate.x, (float) sideCoordinate.y, (float) sideCoordinate.z).color(secondColor).normal(pose.normal(), 0.0f, 1.0f, 0.0f).endVertex();
                vc.vertex(pose.pose(), (float) secondCoordinate.x, (float) secondCoordinate.y, (float) secondCoordinate.z).color(secondColor).normal(pose.normal(), 0.0f, 1.0f, 0.0f).endVertex();
            }
        }

        // quads
        {
            //VertexConsumer vc = buffer.getBuffer(RenderType.lines());

            int bellowCount = train.carriageSpacing.get(front ? carriageIndex - 1 : carriageIndex);
            double straightLengthTop = offsetToTopCenter / bellowCount;// = (top ? offsetToTopCenter : offsetToCenter) / bellowCount;
            double straightLengthBottom = offsetToCenter / bellowCount;
            double halfStraightLengthTop = straightLengthTop / 2;
            double halfStraightLengthBottom = straightLengthBottom / 2;

            double coveredLengthTop = 0.0;
            double coveredLengthBottom = 0.0;

            double flapSidewaysTop = Math.sqrt((flapLength*flapLength) - (halfStraightLengthTop*halfStraightLengthTop));
            double flapSidewaysBottom = Math.sqrt((flapLength*flapLength) - (halfStraightLengthBottom*halfStraightLengthBottom));

            if (Double.isNaN(flapSidewaysTop)) {
                flapSidewaysTop = 0.0;
            }

            if (Double.isNaN(flapSidewaysBottom)) {
                flapSidewaysBottom = 0.0;
            }


//            Vec3 baseOffset = top ? myGlobalTopPos.subtract(myGlobalPos) : Vec3.ZERO;
            Vec3 normalizedForwardTop = centerTopOffset.normalize();
            Vec3 normalizedForwardBottom = centerOffset.normalize();
            Vec3 normalizedRightTop = VecHelper.rotate(new Vec3(normalizedForwardTop.x, 0, normalizedForwardTop.z), 90, Axis.Y);
            normalizedRightTop = new Vec3(normalizedRightTop.x, 0, normalizedRightTop.z).normalize();
            Vec3 normalizedRightBottom = VecHelper.rotate(new Vec3(normalizedForwardBottom.x, 0, normalizedForwardBottom.z), 90, Axis.Y);
            normalizedRightBottom = new Vec3(normalizedRightBottom.x, 0, normalizedRightBottom.z).normalize();

            Vec3 normalizedUp = (centerTopOffset.add(myGlobalTopPos)).subtract((centerOffset.add(myGlobalPos))).normalize();

            //Vec3 normalizedUp = VecHelper.rotate(normalizedForwardBottom, front ? -90 : 90, Axis.X);

            Vec3 intermediate = VecHelper.rotate(normalizedUp, front ? -90 : 90, Axis.X);
            // we want to copy the y/(xz_plane) slope from intermediate to cNFB
            double slope = intermediate.y / intermediate.horizontalDistance();
            double cnfbY = -slope * normalizedForwardBottom.horizontalDistance();
            Vec3 connectorNormalizedForwardBottom = new Vec3(normalizedForwardBottom.x, cnfbY, normalizedForwardBottom.z).normalize();

            // box outline
            float extra = 0.001f;
            float w1 = front ? 0.5f+extra : -0.5f-extra; // width
            float w2 = -w1;
            float h = 16 / 16f+extra; // height
            float d = (float)(4 / 16f)+extra;  // depth

/*            if (!connectorNormalizedForwardBottom.normalize().equals(connectorNormalizedForwardBottom)) {// || !normalizedRightBottom.equals(normalizedRightBottom.normalize())) {
                Railways.LOGGER.error("oops: "+connectorNormalizedForwardBottom.normalize().subtract(connectorNormalizedForwardBottom));
            }*/

            Vec3 centerFrontBottom = realCenterOffset.add(0.5, 0, 0.5);
            Vec3 centerBackBottom = centerFrontBottom.add(connectorNormalizedForwardBottom.scale(-d));
//            Railways.LOGGER.info("cto: "+centerTopOffset+", co: "+centerOffset+", nu: "+normalizedUp);
//            Railways.LOGGER.info(normalizedUp);
            boxOutline(
                pose.pose(),
                pose.normal(),
                (localPos.getY() % 2 == 0) ? (front ? 0xffffa0ff : 0xff0044ff) : (front ? 0xff00ff07 : 0xffff0aba),
                centerFrontBottom.add(normalizedUp.scale(h)).add(normalizedRightBottom.scale(w2)), // ftl
                centerFrontBottom.add(normalizedUp.scale(h)).add(normalizedRightBottom.scale(w1)), // ftr
                centerFrontBottom.add(normalizedRightBottom.scale(w1)), // fbr
                centerFrontBottom.add(normalizedRightBottom.scale(w2)), // fbl
                centerBackBottom.add(normalizedUp.scale(h)).add(normalizedRightBottom.scale(w2)), // btl
                centerBackBottom.add(normalizedUp.scale(h)).add(normalizedRightBottom.scale(w1)), // btr
                centerBackBottom.add(normalizedRightBottom.scale(w1)), // bbr
                centerBackBottom.add(normalizedRightBottom.scale(w2)) // bbl
            );

            for (int i = 0; i < bellowCount; i++) {
                int sideScale = i % 2 == 0 ? -1 : 1;
                Vec3 topAdd = myGlobalTopPos.subtract(myGlobalPos);
                Vec3 firstCoordinateTop = normalizedForwardTop.scale(coveredLengthTop).add(0.5, 0, 0.5).add(topAdd);//myGlobalTopPos.subtract(myGlobalPos));
                Vec3 sideCoordinateTop = firstCoordinateTop.add(normalizedForwardTop.scale(halfStraightLengthTop))
                    .add(normalizedRightTop.scale(flapSidewaysTop * sideScale));
                Vec3 secondCoordinateTop = firstCoordinateTop.add(normalizedForwardTop.scale(straightLengthTop));

                Vec3 firstCoordinateBottom = normalizedForwardBottom.scale(coveredLengthBottom).add(0.5, 0, 0.5);
                Vec3 sideCoordinateBottom = firstCoordinateBottom.add(normalizedForwardBottom.scale(halfStraightLengthBottom))
                    .add(normalizedRightBottom.scale(flapSidewaysBottom * sideScale));
                Vec3 secondCoordinateBottom = firstCoordinateBottom.add(normalizedForwardBottom.scale(straightLengthBottom));
                
                coveredLengthTop += straightLengthTop;
                coveredLengthBottom += straightLengthBottom;

//                Railways.LOGGER.info(firstCoordinateBottom.subtract(firstCoordinateTop).length());

                int firstColor = i % 2 == 0 ? 0xFFFF0000 : 0xFFFFFF00;
                int secondColor = i % 2 == 0 ? 0xFF0000FF : 0xFF00FF00;

                firstColor = secondColor = 0xffffffff;

                RenderSystem.enableTexture();
                Minecraft.getInstance().getTextureManager().getTexture(InventoryMenu.BLOCK_ATLAS).setFilter(false, false);
                RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
                TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                    .apply(new ResourceLocation("minecraft", "block/dried_kelp_side"));
//                CRGuiTextures.TRAIN_HUD_SWITCH_ANDESITE.bind();
                // first rect
                quad(pose.pose(), firstCoordinateTop, sideCoordinateTop, sideCoordinateBottom, firstCoordinateBottom, firstColor, sprite.getU(8), sprite.getV0(), sprite.getU0(), sprite.getV(2));

                // second rect
                quad(pose.pose(), sideCoordinateTop, secondCoordinateTop, secondCoordinateBottom, sideCoordinateBottom, secondColor, sprite.getU1(), sprite.getV0(), sprite.getU(8), sprite.getV(2));

                // first line
//                vc.vertex(pose.pose(), (float) firstCoordinate.x, (float) firstCoordinate.y, (float) firstCoordinate.z).color(firstColor).normal(pose.normal(), 0.0f, 1.0f, 0.0f).endVertex();
//                vc.vertex(pose.pose(), (float) sideCoordinate.x, (float) sideCoordinate.y, (float) sideCoordinate.z).color(firstColor).normal(pose.normal(), 0.0f, 1.0f, 0.0f).endVertex();

                // second line
//                vc.vertex(pose.pose(), (float) sideCoordinate.x, (float) sideCoordinate.y, (float) sideCoordinate.z).color(secondColor).normal(pose.normal(), 0.0f, 1.0f, 0.0f).endVertex();
//                vc.vertex(pose.pose(), (float) secondCoordinate.x, (float) secondCoordinate.y, (float) secondCoordinate.z).color(secondColor).normal(pose.normal(), 0.0f, 1.0f, 0.0f).endVertex();
            }
        }

        ms2.popPose();

        ms.popPose();
        //***************************
        //* End Line Rendering Test *
        //***************************
        ms.popPose();
    }

    /*
    1. ftl-ftr-fbr-fbl
    2. btl-btr-ftr-ftl
    3. fbl-fbr-bbr-bbl
    4. btr-btl-bbl-bbr
     */
    private static void boxOutline(Matrix4f pose, Matrix3f normal, int color, Vec3 ftl, Vec3 ftr, Vec3 fbr, Vec3 fbl, Vec3 btl, Vec3 btr, Vec3 bbr, Vec3 bbl) {
        quadLines(pose, normal, ftl, ftr, fbr, fbl, color);
        quadLines(pose, normal, btl, btr, ftr, ftl, color);
        quadLines(pose, normal, fbl, fbr, bbr, bbl, color);
        quadLines(pose, normal, btr, btl, bbl, bbr, color);
    }

    private static void quadLines(Matrix4f pose, Matrix3f normal, Vec3 tl, Vec3 tr, Vec3 br, Vec3 bl, int color) {
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buf = tess.getBuilder();
        buf.begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);
//        VertexConsumer vc = buffer.getBuffer(RenderType.solid());

        buf.vertex(pose, (float) tl.x, (float) tl.y, (float) tl.z).color(color).endVertex();
        buf.vertex(pose, (float) tr.x, (float) tr.y, (float) tr.z).color(color).endVertex();
        buf.vertex(pose, (float) br.x, (float) br.y, (float) br.z).color(color).endVertex();
        buf.vertex(pose, (float) bl.x, (float) bl.y, (float) bl.z).color(color).endVertex();
        buf.vertex(pose, (float) tl.x, (float) tl.y, (float) tl.z).color(color).endVertex();

        tess.end();
        RenderSystem.disableBlend();
    }

    private static void quad(Matrix4f pose, Vec3 tl, Vec3 tr, Vec3 br, Vec3 bl, int color, float tlu, float tlv, float bru, float brv) {
        quad(pose, tl, tr, br, bl, color, tlu, tlv, bru, brv, true);
    }

    private static void quad(Matrix4f pose, Vec3 tl, Vec3 tr, Vec3 br, Vec3 bl, int color, float tlu, float tlv, float bru, float brv, boolean doubleSided) {
        float tru = bru;
        float trv = tlv;

        float blu = tlu;
        float blv = brv;

        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buf = tess.getBuilder();
        buf.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR_TEX);
//        VertexConsumer vc = buffer.getBuffer(RenderType.solid());

        // triangle 1 (tl, tr, br)
        buf.vertex(pose, (float) tl.x, (float) tl.y, (float) tl.z).color(color).uv(tlu, tlv).endVertex();
        buf.vertex(pose, (float) tr.x, (float) tr.y, (float) tr.z).color(color).uv(tru, trv).endVertex();
        buf.vertex(pose, (float) br.x, (float) br.y, (float) br.z).color(color).uv(bru, brv).endVertex();

        // triangle 2 (tl, br, bl)
        buf.vertex(pose, (float) tl.x, (float) tl.y, (float) tl.z).color(color).uv(tlu, tlv).endVertex();
        buf.vertex(pose, (float) br.x, (float) br.y, (float) br.z).color(color).uv(bru, brv).endVertex();
        buf.vertex(pose, (float) bl.x, (float) bl.y, (float) bl.z).color(color).uv(blu, blv).endVertex();

        if (doubleSided) {
            // reverse side
            // triangle 3 (tr, tl, br)
            buf.vertex(pose, (float) tr.x, (float) tr.y, (float) tr.z).color(color).uv(tru, trv).endVertex();
            buf.vertex(pose, (float) tl.x, (float) tl.y, (float) tl.z).color(color).uv(tlu, tlv).endVertex();
            buf.vertex(pose, (float) br.x, (float) br.y, (float) br.z).color(color).uv(bru, brv).endVertex();

            //triangle 4 (tl, bl, br)
            buf.vertex(pose, (float) tl.x, (float) tl.y, (float) tl.z).color(color).uv(tlu, tlv).endVertex();
            buf.vertex(pose, (float) bl.x, (float) bl.y, (float) bl.z).color(color).uv(blu, blv).endVertex();
            buf.vertex(pose, (float) br.x, (float) br.y, (float) br.z).color(color).uv(bru, brv).endVertex();
        }

        tess.end();
        RenderSystem.disableBlend();
    }
}
