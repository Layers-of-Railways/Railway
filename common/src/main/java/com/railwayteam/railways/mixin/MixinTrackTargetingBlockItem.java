package com.railwayteam.railways.mixin;

import com.railwayteam.railways.registry.CREdgePointTypes;
import com.simibubi.create.content.logistics.trains.GraphLocation;
import com.simibubi.create.content.logistics.trains.management.edgePoint.EdgePointType;
import com.simibubi.create.content.logistics.trains.management.edgePoint.TrackTargetingBlockItem;
import com.simibubi.create.content.logistics.trains.track.BezierTrackPointLocation;
import com.simibubi.create.content.logistics.trains.track.TrackBlock;
import com.simibubi.create.content.logistics.trains.track.TrackShape;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

@Mixin(value = TrackTargetingBlockItem.class, remap = false)
public class MixinTrackTargetingBlockItem {

    private static final List<TrackShape> acceptableShapes = List.of(
        TrackShape.XO,
        TrackShape.ZO,
        TrackShape.PD,
        TrackShape.ND,
        TrackShape.AN,
        TrackShape.AS,
        TrackShape.AE,
        TrackShape.AW
    );

    @Inject(method = "withGraphLocation", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/trains/ITrackBlock;getTrackAxes(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Ljava/util/List;", remap = true), cancellable = true)
    private static void checkGraphLocation(Level level, BlockPos pos, boolean front, BezierTrackPointLocation targetBezier, EdgePointType<?> type,
                                           BiConsumer<TrackTargetingBlockItem.OverlapResult, GraphLocation> callback, CallbackInfo ci) {
        if (type != CREdgePointTypes.COUPLER)
            return;
        TrackTargetingBlockItem.OverlapResult not_straight = TrackTargetingBlockItem.OverlapResult.valueOf("NOT_STRAIGHT");
        if (targetBezier != null) {
            callback.accept(not_straight, null);
            ci.cancel();
        }

        TrackShape shape = level.getBlockState(pos).getValue(TrackBlock.SHAPE);
        if (!acceptableShapes.contains(shape)) {
            callback.accept(not_straight, null);
            ci.cancel();
        }
    }

    //Ripped from LudoCrypt's Noteblock Expansion
    // (https://github.com/LudoCrypt/Noteblock-Expansion-Forge/blob/main/src/main/java/net/ludocrypt/nbexpand/mixin/NoteblockInstrumentMixin.java)
    @Mixin(value = TrackTargetingBlockItem.OverlapResult.class, remap = false)
    public static class MixinOverlapResult {
        @Shadow
        @Final
        @Mutable
        private static TrackTargetingBlockItem.OverlapResult[] $VALUES;

        private static final TrackTargetingBlockItem.OverlapResult NOT_STRAIGHT = snr$addResult("NOT_STRAIGHT", "track_target.not_straight");

        @Invoker("<init>")
        public static TrackTargetingBlockItem.OverlapResult snr$invokeInit(String internalName, int internalId, String feedback) {
            throw new AssertionError();
        }

        private static TrackTargetingBlockItem.OverlapResult snr$addResult(String internalName, String feedback) {
            ArrayList<TrackTargetingBlockItem.OverlapResult> results = new ArrayList<TrackTargetingBlockItem.OverlapResult>(Arrays.asList($VALUES));
            TrackTargetingBlockItem.OverlapResult result = snr$invokeInit(internalName, results.get(results.size() - 1).ordinal() + 1, feedback);
            results.add(result);
            $VALUES = results.toArray(new TrackTargetingBlockItem.OverlapResult[0]);
            return result;
        }
    }
}
