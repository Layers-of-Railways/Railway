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

package com.railwayteam.railways.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.content.custom_tracks.casing.CasingRenderUtils;
import com.railwayteam.railways.mixin_interfaces.IHasTrackCasing;
import com.railwayteam.railways.registry.CRBlockPartials;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackBlockEntity;
import com.simibubi.create.content.trains.track.TrackMaterial.TrackType;
import com.simibubi.create.content.trains.track.TrackShape;
import com.simibubi.create.content.trains.track.TrackVisual;
import dev.engine_room.flywheel.api.visual.BlockEntityVisual;
import dev.engine_room.flywheel.api.visual.ShaderLightVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import dev.engine_room.flywheel.lib.visual.AbstractVisual;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.data.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

import static com.railwayteam.railways.content.custom_tracks.casing.CasingRenderUtils.casingPositions;
import static com.railwayteam.railways.registry.CRTrackMaterials.CRTrackType.NARROW_GAUGE;
import static com.railwayteam.railways.registry.CRTrackMaterials.CRTrackType.WIDE_GAUGE;

@Mixin(value = TrackVisual.class, remap = false)
public abstract class MixinTrackVisual extends AbstractVisual implements BlockEntityVisual<TrackBlockEntity>, ShaderLightVisual {
    public MixinTrackVisual(VisualizationContext ctx, Level level, float partialTick) {
        super(ctx, level, partialTick);
    }

    @Shadow
    public abstract void _delete();

    @Shadow
    @Final
    protected TrackBlockEntity blockEntity;

    @Shadow
    @Final
    protected BlockPos visualPos;

    @Shadow
    @Final
    protected BlockPos pos;

    private final List<Pair<TransformedInstance, BlockPos>> casingData = new ArrayList<>();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onCtor(VisualizationContext context, TrackBlockEntity track, float partialTick, CallbackInfo ci) {
        railways$makeCasingData(true);
    }

    @Inject(method = "update", at = @At(value = "RETURN", ordinal = 0))
    private void updateWithoutConnections(CallbackInfo ci) { //otherwise it visually stays when an encased track is broken
        this._delete();
        railways$makeCasingData(false);
    }

    @Inject(method = "update", at = @At(value = "RETURN", ordinal = 1))
    private void updateWithConnections(CallbackInfo ci) {
        railways$makeCasingData(true);
    }

    @Inject(method = "_delete", at = @At("HEAD"))
    private void railways$_delete(CallbackInfo ci) {
        casingData.forEach((data) -> data.getFirst().delete());
        casingData.clear();
    }

    @Unique
    private void railways$makeCasingData(boolean connections) {
        PoseStack ms = new PoseStack();
        TransformStack.of(ms)
            .translate(visualPos)
            .nudge((int) this.pos.asLong());

        SlabBlock casingBlock = ((IHasTrackCasing) this.blockEntity).getTrackCasing();
        if (casingBlock != null) {
			BlockState state = blockEntity.getBlockState();
			
            TrackShape shape = state.getValue(TrackBlock.SHAPE);
            if (CRBlockPartials.TRACK_CASINGS.containsKey(shape)) {
                ms.pushPose();
                if (this.blockEntity.isTilted()) {
                    double angle = this.blockEntity.tilt.smoothingAngle.get();
                    switch (state.getValue(TrackBlock.SHAPE)) {
                        case ZO -> TransformStack.of(ms)
                            .rotateXDegrees((float) -angle);
                        case XO -> TransformStack.of(ms)
                            .rotateZDegrees((float) angle);
                    }
                }
                TrackType trackType = null;
                if (state.getBlock() instanceof TrackBlock trackBlock)
                    trackType = trackBlock.getMaterial().trackType;

                CRBlockPartials.TrackCasingSpec spec = CRBlockPartials.TRACK_CASINGS.get(shape);
                if (((IHasTrackCasing) this.blockEntity).isAlternate())
                    spec = spec.getNonNullAltSpec(trackType);
                else
                    spec = spec.getFor(trackType);
                PartialModel rawCasingModel = spec.model;
                CRBlockPartials.ModelTransform transform = spec.transform;

                TransformedInstance casingInstance = CasingRenderUtils.makeCasingInstance(rawCasingModel, casingBlock, instancerProvider());
                casingInstance.setTransform(ms)
                    .rotateX(transform.rx())
                    .rotateY(transform.ry())
                    .rotateZ(transform.rz())
                    .translate(transform.x(), transform.y(), transform.z());
                casingData.add(Pair.of(casingInstance, this.pos));

                for (CRBlockPartials.ModelTransform additionalTransform : spec.additionalTransforms) {
                    TransformedInstance additionalInstance = CasingRenderUtils.makeCasingInstance(rawCasingModel, casingBlock, instancerProvider());
                    additionalInstance.setTransform(ms)
                        .rotateX(additionalTransform.rx())
                        .rotateY(additionalTransform.ry())
                        .rotateZ(additionalTransform.rz())
                        .translate(additionalTransform.x(), additionalTransform.y(), additionalTransform.z());
                    casingData.add(Pair.of(additionalInstance, this.pos.offset(Mth.floor(additionalTransform.x()), Mth.floor(additionalTransform.y()), Mth.floor(additionalTransform.z()))));
                }
                ms.popPose();
            }
        }

        if (connections) {
            for (BezierConnection bc : this.blockEntity.getConnections().values()) {
                if (!bc.isPrimary()) continue;
                casingBlock = ((IHasTrackCasing) bc).getTrackCasing();
                if (casingBlock != null) {
                    int heightDiff = Math.abs(bc.bePositions.get(false).getY() - bc.bePositions.get(true).getY());
                    double shiftDown = ((IHasTrackCasing) bc).isAlternate() && heightDiff > 0 ? -0.25 : 0;
                    if (heightDiff / bc.getLength() <= 4 / 30d) {
                        for (Vec3 pos : casingPositions(bc)) {
                            TransformedInstance casingInstance = CasingRenderUtils.makeCasingInstance(heightDiff==0 ? CRBlockPartials.TRACK_CASING_FLAT :
                                CRBlockPartials.TRACK_CASING_FLAT_THICK, casingBlock, instancerProvider());
                            casingInstance.setTransform(ms)
                                .translate(0, shiftDown, 0)
                                .translate(pos.x, pos.y, pos.z)
                                .scale(1.001f);
                            BlockPos relativePos = BlockPos.containing(this.pos.getX() + pos.x, this.pos.getY() + pos.y, this.pos.getZ() + pos.z);
                            casingData.add(Pair.of(casingInstance, relativePos));
                        }
                    } else {
                        BezierConnection.SegmentAngles segments = bc.getBakedSegments();

                        for (int i = 1; i < segments.length; i++) {
                            if (i % 2 == 0) continue;

                            TransformedInstance casingInstance = CasingRenderUtils.makeCasingInstance(heightDiff==0 ? CRBlockPartials.TRACK_CASING_FLAT :
                                CRBlockPartials.TRACK_CASING_FLAT_THICK, casingBlock, instancerProvider());
                            casingInstance.setTransform(ms)
                                .mul(segments.tieTransform[i])
                                .translate(0, (i % 4) * 0.001f, 0)
                                .translate(0, shiftDown, 0)
                                .scale(1.001f);
                            BlockPos relativePos = segments.lightPosition[i].offset(this.pos);
                            casingData.add(Pair.of(casingInstance, relativePos));

                            TrackType trackType = bc.getMaterial().trackType;
                            if (trackType == WIDE_GAUGE) {
                                for (boolean first : Iterate.trueAndFalse) {
                                    for (boolean inner : Iterate.trueAndFalse) {
                                        PoseStack.Pose transform = segments.railTransforms[i].get(first);

                                        TransformedInstance casingInstance2 = CasingRenderUtils.makeCasingInstance(heightDiff == 0 ? CRBlockPartials.TRACK_CASING_FLAT :
                                            CRBlockPartials.TRACK_CASING_FLAT_THICK, casingBlock, instancerProvider());
                                        casingInstance2.setTransform(ms)
                                            .mul(transform)
                                            .translate(0, (i % 4) * 0.001f, 0)
                                            .translate((first ? -(61 / 64.) : -(1 / 32.)) + (inner ? 0 : (first ? 1 : -1)), shiftDown, 0);
                                        BlockPos relativePos2 = segments.lightPosition[i].offset(this.pos);
                                        casingData.add(Pair.of(casingInstance2, relativePos2));
                                    }
                                }
                            } else {
                                for (boolean first : Iterate.trueAndFalse) {
                                    PoseStack.Pose transform = segments.railTransforms[i].get(first);

                                    TransformedInstance casingInstance2 = CasingRenderUtils.makeCasingInstance(heightDiff == 0 ? CRBlockPartials.TRACK_CASING_FLAT :
                                        CRBlockPartials.TRACK_CASING_FLAT_THICK, casingBlock, instancerProvider());
                                    casingInstance2.setTransform(ms)
                                        .mul(transform)
                                        .translate(0, (i % 4) * 0.001f, 0)
                                        .translate(-0.5 + (trackType == NARROW_GAUGE ? (first ? 0.5 : -0.5) : 0), shiftDown, 0);
                                    BlockPos relativePos2 = segments.lightPosition[i].offset(this.pos);
                                    casingData.add(Pair.of(casingInstance2, relativePos2));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
