package com.railwayteam.railways.mixin.client;

import com.jozufozu.flywheel.util.transform.TransformStack;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.mixin_interfaces.IHasTrackCasing;
import com.railwayteam.railways.registry.CRBlockPartials;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import com.simibubi.create.content.trains.station.StationRenderer;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.simibubi.create.content.trains.track.TrackShape;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StationRenderer.class)
public class MixinStationRenderer {
    @Inject(method = "renderSafe(Lcom/simibubi/create/content/trains/station/StationBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/render/CachedBufferer;partial(Lcom/jozufozu/flywheel/core/PartialModel;Lnet/minecraft/world/level/block/state/BlockState;)Lcom/simibubi/create/foundation/render/SuperByteBuffer;"))
    private void shiftAssemblyOverlayOnEncasedTracks(
        StationBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay,
        CallbackInfo ci, @Local(name = "currentPos") MutableBlockPos currentPos, @Local(name = "trackState") BlockState trackState) {
        ms.pushPose();

        if (trackState.getBlock() instanceof TrackBlock trackBlock
            && be.getLevel().getBlockEntity(currentPos) instanceof IHasTrackCasing casing && casing.getTrackCasing() != null) {
            TrackShape shape = trackState.getValue(TrackBlock.SHAPE);
            CRBlockPartials.TrackCasingSpec spec = CRBlockPartials.TRACK_CASINGS.get(shape);
            TrackMaterial.TrackType trackType = trackBlock.getMaterial().trackType;
            if (spec != null)
                TransformStack.cast(ms)
                    .translate(
                        spec.getXShift(trackType),
                        (spec.getTopSurfacePixelHeight(trackType, casing.isAlternate()) - 2) / 16f,
                        spec.getZShift(trackType)
                    );
        }

    }

    @Inject(method = "renderSafe(Lcom/simibubi/create/content/trains/station/StationBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/render/SuperByteBuffer;renderInto(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V", shift = At.Shift.AFTER))
    private void cleanup(StationBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay, CallbackInfo ci) {
        ms.popPose();
    }
}
