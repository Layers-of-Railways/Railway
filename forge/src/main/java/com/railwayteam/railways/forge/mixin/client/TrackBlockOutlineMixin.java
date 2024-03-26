package com.railwayteam.railways.forge.mixin.client;

import com.railwayteam.railways.content.custom_tracks.monorail.CustomTrackBlockOutline;
import com.simibubi.create.content.trains.track.TrackBlockOutline;
import net.minecraftforge.client.event.RenderHighlightEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TrackBlockOutline.class)
public class TrackBlockOutlineMixin {
    @Inject(method = "drawCustomBlockSelection", at = @At("HEAD"), cancellable = true, remap = false)
    private static void railways$cancel(RenderHighlightEvent.Block event, CallbackInfo ci) {
        if (CustomTrackBlockOutline.skipCustomRendering())
            ci.cancel();
    }
}
