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

import com.llamalad7.mixinextras.sugar.Local;
import com.railwayteam.railways.content.custom_tracks.phantom.PhantomSpriteManager;
import com.railwayteam.railways.mixin_interfaces.IHasTrackCasing;
import com.railwayteam.railways.registry.CRBlockPartials;
import com.railwayteam.railways.registry.CRTrackMaterials;
import com.railwayteam.railways.registry.CRTrackMaterials.CRTrackType;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.BezierTrackPointLocation;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackBlockEntity;
import com.simibubi.create.content.trains.track.TrackMaterial.TrackType;
import com.simibubi.create.content.trains.track.TrackShape;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour.RenderedTrackOverlayType;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.Affine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TrackBlock.class)
public class MixinTrackBlockClient {
    @Inject(
        method = "prepareTrackOverlay",
        at = @At(
            value = "INVOKE_ASSIGN",
            target = "Ldev/engine_room/flywheel/lib/transform/Affine;translate(FFF)Ldev/engine_room/flywheel/lib/transform/Translate;",
            ordinal = 0
        ),
        remap = false
    ) // Yeah, it's nice to shift the overlays up, but don't crash the game for it.
    private <Self extends Affine<Self>> void bezierShiftTrackOverlay(Affine<Self> affine, BlockGetter world, BlockPos pos, BlockState state, BezierTrackPointLocation bezierPoint, AxisDirection direction, RenderedTrackOverlayType type, CallbackInfoReturnable<PartialModel> cir, @Local BezierConnection bc) {
        IHasTrackCasing casingBc = (IHasTrackCasing) bc;
        if (bc.getMaterial().trackType == CRTrackType.MONORAIL) {
            affine.translate(0, 14/16f, 0);
            return;
        }
        // Don't shift up if the curve is a slope and the casing is under the track, rather than in it
        if (casingBc.getTrackCasing() != null) {
            if (bc.bePositions.getFirst().getY() == bc.bePositions.getSecond().getY()) {
                affine.translate(0, 1 / 16f, 0);
            } else if (!casingBc.isAlternate()) {
                affine.translate(0, 4 / 16f, 0);
            }
        }
    }

    @Inject(method = "prepareTrackOverlay", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/track/TrackRenderer;getModelAngles(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;", remap = true), remap = false)
    private <Self extends Affine<Self>> void blockShiftTrackOverlay(Affine<Self> affine, BlockGetter world, BlockPos pos, BlockState state, BezierTrackPointLocation bezierPoint, AxisDirection direction, RenderedTrackOverlayType type, CallbackInfoReturnable<PartialModel> cir) {
        if (bezierPoint == null && state.getBlock() instanceof TrackBlock trackBlock && trackBlock.getMaterial().trackType == CRTrackMaterials.CRTrackType.MONORAIL) {
            affine.translate(0, 14/16f, 0);
            return;
        }
        if (bezierPoint == null && world.getBlockEntity(pos) instanceof TrackBlockEntity trackTE && state.getBlock() instanceof TrackBlock trackBlock) {
            IHasTrackCasing casingTE = (IHasTrackCasing) trackTE;
            TrackShape shape = state.getValue(TrackBlock.SHAPE);
            if (casingTE.getTrackCasing() != null) {
                CRBlockPartials.TrackCasingSpec spec = CRBlockPartials.TRACK_CASINGS.get(shape);
                TrackType trackType = trackBlock.getMaterial().trackType;
                if (spec != null)
                    affine.translate(
                        spec.getXShift(trackType),
                        (spec.getTopSurfacePixelHeight(trackType, casingTE.isAlternate()) - 2)/16f,
                        spec.getZShift(trackType)
                    );
            }
        }
    }

    @Inject(method = "prepareTrackOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;floor(D)I"), cancellable = true)
    private <Self extends Affine<Self>> void skipInvisiblePhantoms(Affine<Self> affine, BlockGetter world, BlockPos pos, BlockState state, BezierTrackPointLocation bezierPoint, AxisDirection direction, RenderedTrackOverlayType type, CallbackInfoReturnable<PartialModel> cir, @Local BezierConnection bc) {
        if (bc.getMaterial() == CRTrackMaterials.PHANTOM && !PhantomSpriteManager.isVisible())
            cir.setReturnValue(null);
    }
}
