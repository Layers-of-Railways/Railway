package com.railwayteam.railways.fabric.mixin.client;

import com.railwayteam.railways.content.custom_tracks.monorail.CustomTrackBlockOutline;
import com.simibubi.create.content.trains.track.TrackBlockOutline;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TrackBlockOutline.class)
public class TrackBlockOutlineMixin {


    @Inject(method = "drawCustomBlockSelection", at = @At("HEAD"), cancellable = true)
    private static void railways$cancel(CallbackInfoReturnable<Boolean> cir) {
        if (CustomTrackBlockOutline.skipCustomRendering())
            cir.setReturnValue(false);
    }
}
