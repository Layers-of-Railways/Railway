/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2025 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.content.custom_tracks.phantom.PhantomSpriteManager;
import com.railwayteam.railways.mixin_interfaces.IHasTrackCasing;
import com.railwayteam.railways.registry.CRBlockPartials;
import com.railwayteam.railways.registry.CRBlockPartials.TrackCasingSpec;
import com.railwayteam.railways.registry.CRTrackMaterials;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraphLocation;
import com.simibubi.create.content.trains.signal.TrackEdgePoint;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.BezierTrackPointLocation;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackBlockEntity;
import com.simibubi.create.content.trains.track.TrackMaterial.TrackType;
import com.simibubi.create.content.trains.track.TrackRenderer;
import com.simibubi.create.content.trains.track.TrackShape;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.levelWrappers.SchematicLevel;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.ponder.api.level.PonderLevel;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public class CustomTrackOverlayRendering {
    public static final Map<EdgePointType<?>, PartialModel> CUSTOM_OVERLAYS = new HashMap<>();

    public static void register(EdgePointType<?> edgePointType, PartialModel model) {
        CUSTOM_OVERLAYS.put(edgePointType, model);
    }

    @OnlyIn(Dist.CLIENT)
    public static void renderOverlay(LevelAccessor level, BlockPos pos, Direction.AxisDirection direction,
                                     BezierTrackPointLocation bezier, PoseStack ms, MultiBufferSource buffer, int light, int overlay,
                                     EdgePointType<?> type, float scale) {
        if (CUSTOM_OVERLAYS.containsKey(type))
            renderOverlay(level, pos, direction, bezier, ms, buffer, light, overlay, CUSTOM_OVERLAYS.get(type), scale, false);
    }

    @OnlyIn(Dist.CLIENT)
    public static void renderOverlay(LevelAccessor level, BlockPos pos, Direction.AxisDirection direction,
                                     BezierTrackPointLocation bezier, PoseStack ms, MultiBufferSource buffer, int light, int overlay,
                                     PartialModel model, float scale) {
        renderOverlay(level, pos, direction, bezier, ms, buffer, light, overlay, model, scale, false);
    }

    //Copied from TrackTargetingBehaviour
    @OnlyIn(Dist.CLIENT)
    public static void renderOverlay(LevelAccessor level, BlockPos pos, Direction.AxisDirection direction,
                              BezierTrackPointLocation bezier, PoseStack ms, MultiBufferSource buffer, int light, int overlay,
                              PartialModel model, float scale, boolean offsetToSide) {
        if (level instanceof SchematicLevel && !(level instanceof PonderLevel))
            return;

        BlockState trackState = level.getBlockState(pos);
        Block block = trackState.getBlock();
        if (!(block instanceof ITrackBlock))
            return;

        ms.pushPose();

        PartialModel partial = prepareTrackOverlay(level, pos, trackState, bezier, direction, ms, model);
        if (partial != null)
            CachedBuffers.partial(partial, trackState)
                .translate(.5, 0, .5)
                .scale(scale)
                .translate(offsetToSide ? .5 : -.5, 0, -.5)
                .light(LevelRenderer.getLightColor(level, pos))
                .renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()));

        ms.popPose();
    }

    //Copied from TrackBlock
    @OnlyIn(Dist.CLIENT)
    public static PartialModel prepareTrackOverlay(BlockGetter world, BlockPos pos, BlockState state,
                                                   BezierTrackPointLocation bezierPoint, Direction.AxisDirection direction,
                                                   PoseStack ms, PartialModel model) {
        var msr = TransformStack.of(ms);

        Vec3 axis = null;
        Vec3 diff = null;
        Vec3 normal = null;
        Vec3 offset = null;

        if (bezierPoint != null && world.getBlockEntity(pos) instanceof TrackBlockEntity trackTE) {
            BezierConnection bc = trackTE.getConnections().get(bezierPoint.curveTarget());
            if (bc != null) {
                if (bc.getMaterial() == CRTrackMaterials.PHANTOM && !PhantomSpriteManager.isVisible())
                    return null;

                double length = Mth.floor(bc.getLength() * 2);
                int seg = bezierPoint.segment() + 1;
                double t = seg / length;
                double tpre = (seg - 1) / length;
                double tpost = (seg + 1) / length;

                offset = bc.getPosition(t);
                normal = bc.getNormal(t);
                diff = bc.getPosition(tpost)
                    .subtract(bc.getPosition(tpre))
                    .normalize();

                msr.translate(offset.subtract(Vec3.atBottomCenterOf(pos)));
                msr.translate(0, -4 / 16f, 0);
                // Translate more for slabs or monorails
                IHasTrackCasing casingBc = (IHasTrackCasing) bc;
                if (bc.getMaterial().trackType == CRTrackMaterials.CRTrackType.MONORAIL) {
                    msr.translate(0, 14/16f, 0);
                } else if (casingBc.getTrackCasing() != null) {
                    // Don't shift up if the curve is a slope and the casing is under the track, rather than in it
                    if (bc.bePositions.getFirst().getY() == bc.bePositions.getSecond().getY()) {
                        msr.translate(0, 1 / 16f, 0);
                    } else if (!casingBc.isAlternate()) {
                        msr.translate(0, 4 / 16f, 0);
                    }
                }
            } else
                return null;
        }

        if (normal == null) {
            axis = state.getValue(TrackBlock.SHAPE)
                .getAxes()
                .get(0);
            diff = axis.scale(direction.getStep())
                .normalize();
            normal = ((ITrackBlock) state.getBlock()).getUpNormal(world, pos, state);
        }

        if (state.getBlock() instanceof TrackBlock track && track.getMaterial() == CRTrackMaterials.PHANTOM && !PhantomSpriteManager.isVisible())
            return null;

        //Shift for casings and monorails
        if (bezierPoint == null && state.getBlock() instanceof TrackBlock trackBlock && trackBlock.getMaterial().trackType == CRTrackMaterials.CRTrackType.MONORAIL) {
            msr.translate(0, 14/16f, 0);
        } else if (bezierPoint == null && world.getBlockEntity(pos) instanceof TrackBlockEntity trackTE && state.getBlock() instanceof TrackBlock trackBlock) {
            IHasTrackCasing casingTE = (IHasTrackCasing) trackTE;
            TrackShape shape = state.getValue(TrackBlock.SHAPE);
            if (casingTE.getTrackCasing() != null) {
                TrackCasingSpec spec = CRBlockPartials.TRACK_CASINGS.get(shape);
                TrackType trackType = trackBlock.getMaterial().trackType;
                if (spec != null)
                    msr.translate(
                        spec.getXShift(trackType),
                        (spec.getTopSurfacePixelHeight(trackType, casingTE.isAlternate()) - 2) / 16f,
                        spec.getZShift(trackType)
                    );
            }
        }

        Vec3 angles = TrackRenderer.getModelAngles(normal, diff);

        msr.center()
            .rotateY((float) angles.y)
            .rotateX((float) angles.x)
            .uncenter();

        if (axis != null)
            msr.translate(0, axis.y != 0 ? 7 / 16f : 0, axis.y != 0 ? direction.getStep() * 2.5f / 16f : 0);
        else {
            msr.translate(0, 4 / 16f, 0);
            if (direction == Direction.AxisDirection.NEGATIVE)
                msr.rotateCentered(Mth.PI, Direction.UP);
        }

        if (bezierPoint == null && world.getBlockEntity(pos) instanceof TrackBlockEntity trackTE
            && trackTE.isTilted()) {
            double yOffset = 0;
            for (BezierConnection bc : trackTE.getConnections().values())
                yOffset += bc.starts.getFirst().y - pos.getY();
            msr.center()
                .rotateXDegrees((float) ((double) -direction.getStep() * trackTE.tilt.smoothingAngle.get()))
                .uncenter()
                .translate(0, yOffset / 2, 0);
        }

        return model;
    }

    public static boolean overlayWillOverlap(TrackTargetingBehaviour<? extends TrackEdgePoint> target) {
        try {
            TrackGraphLocation graphLocation = target.determineGraphLocation();
            TrackEdge edge = graphLocation.graph.getConnectionsFrom(graphLocation.graph.locateNode(graphLocation.edge.getFirst())).get(graphLocation.graph.locateNode(graphLocation.edge.getSecond()));
            for (TrackEdgePoint edgePoint : edge.getEdgeData().getPoints()) {
                try {
                    if (Math.abs(edgePoint.getLocationOn(edge) - (target.getEdgePoint() != null ? target.getEdgePoint().getLocationOn(edge) : graphLocation.position)) < .75 && edgePoint != target.getEdgePoint() && !edgePoint.getId().equals(target.getEdgePoint().getId())) {
                        return true;
                    }
                } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}

        return false;
    }
}
