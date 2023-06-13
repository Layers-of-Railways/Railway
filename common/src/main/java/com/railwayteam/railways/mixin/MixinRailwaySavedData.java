package com.railwayteam.railways.mixin;

import com.simibubi.create.content.trains.RailwaySavedData;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.DimensionPalette;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = RailwaySavedData.class, remap = false)
public class MixinRailwaySavedData {
    @Inject(method = "lambda$load$5", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"),
        cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void snr$deleteEmptyTrains(RailwaySavedData sd, DimensionPalette dimensions, CompoundTag c,
                                              CallbackInfo ci, Train train) { // delete trains with 0 carriages
        if (train.carriages.isEmpty()) // just don't add it to the list of trains
            ci.cancel();
    }
}
