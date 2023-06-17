package com.railwayteam.railways.mixin;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.mixin_interfaces.ISwitchDisabledEdge;
import com.simibubi.create.content.trains.graph.TrackEdge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TrackEdge.class, remap = false)
public class MixinTrackEdge {
    /*
    done: Other and this need to be flipped in certain calls

    Needs to be flipped:
    - TravellingPoints.java # 313 (reverse travel)

    Does not need to be flipped:
    - TravellingPoints.java # 141 (pretty sure)
    - Navigation.java

    Needs to not be applied:
    - SignalPropagator
     */
    @Inject(method = "canTravelTo", at = @At("HEAD"), cancellable = true)
    private void travelThroughSwitches(TrackEdge other, CallbackInfoReturnable<Boolean> cir) {
        String className = Thread.currentThread().getStackTrace()[3].getClassName();
        if (className.equals("com.simibubi.create.content.trains.signal.SignalPropagator"))
            return;
        if (ISwitchDisabledEdge.isDisabled(Railways.trackEdgeTemporarilyFlipped ? ((TrackEdge) (Object) this) : other))// || ISwitchDisabledEdge.isDisabled((TrackEdge) (Object) this))
            cir.setReturnValue(false);
        Railways.trackEdgeTemporarilyFlipped = false;
    }
}
