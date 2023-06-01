package com.railwayteam.railways.mixin.client;

import com.jozufozu.flywheel.core.PartialModel;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.mixin_interfaces.IHasTrackCasing;
import com.railwayteam.railways.registry.CRBlockPartials;
import com.railwayteam.railways.registry.CRTrackMaterials;
import com.simibubi.create.content.trains.track.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

//import static com.railwayteam.railways.content.coupling.CouplerOverlayType.getCouplerOverlayType;

@Mixin(value = TrackBlock.class, remap = false)
public class MixinTrackBlockClient {

    @Inject(method = "prepareTrackOverlay", at = @At(value = "INVOKE_ASSIGN", target = "Lcom/jozufozu/flywheel/util/transform/TransformStack;translate(DDD)Ljava/lang/Object;", ordinal = 0),
        locals = LocalCapture.CAPTURE_FAILSOFT, remap = false) // Yeah, it's nice to shift the overlays up, but don't crash the game for it.
    private void bezierShiftTrackOverlay(BlockGetter world, BlockPos pos, BlockState state, BezierTrackPointLocation bezierPoint,
                                         Direction.AxisDirection direction,PoseStack ms, TrackTargetingBehaviour.RenderedTrackOverlayType type,
                                         CallbackInfoReturnable<PartialModel> cir, TransformStack msr, Vec3 axis, Vec3 diff, Vec3 normal,
                                         Vec3 offset,TrackBlockEntity trackTE, BezierConnection bc) {
        IHasTrackCasing casingBc = (IHasTrackCasing) bc;
        if (bc.getMaterial().trackType == CRTrackMaterials.CRTrackType.MONORAIL) {
            msr.translate(0, 14/16f, 0);
            return;
        }
        // Don't shift up if the curve is a slope and the casing is under the track, rather than in it
        if (casingBc.getTrackCasing() != null) {
            if (bc.tePositions.getFirst().getY() == bc.tePositions.getSecond().getY()) {
                msr.translate(0, 1 / 16f, 0);
            } else if (!casingBc.isAlternate()) {
                msr.translate(0, 4 / 16f, 0);
            }
        }
    }

    @Inject(method = "prepareTrackOverlay", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/track/TrackRenderer;getModelAngles(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;", remap = true),
        locals = LocalCapture.CAPTURE_FAILSOFT, remap = false)
    private void blockShiftTrackOverlay(BlockGetter world, BlockPos pos, BlockState state, BezierTrackPointLocation bezierPoint, Direction.AxisDirection direction, PoseStack ms, TrackTargetingBehaviour.RenderedTrackOverlayType type, CallbackInfoReturnable<PartialModel> cir, TransformStack msr) {
        if (bezierPoint == null && state.getBlock() instanceof TrackBlock trackBlock && trackBlock.getMaterial().trackType == CRTrackMaterials.CRTrackType.MONORAIL) {
            msr.translate(0, 14/16f, 0);
            return;
        }
        if (bezierPoint == null && world.getBlockEntity(pos) instanceof TrackBlockEntity trackTE) {
            IHasTrackCasing casingTE = (IHasTrackCasing) trackTE;
            TrackShape shape = state.getValue(TrackBlock.SHAPE);
            if (casingTE.getTrackCasing() != null) {
                CRBlockPartials.TrackCasingSpec spec = CRBlockPartials.TRACK_CASINGS.get(shape);
                if (spec != null)
                    msr.translate(spec.getXShift(), (spec.getTopSurfacePixelHeight(casingTE.isAlternate()) - 2)/16f, spec.getZShift());
            }
        }
    }
}
