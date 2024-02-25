package com.railwayteam.railways.mixin;

import com.railwayteam.railways.mixin_interfaces.ISwitchDisabledEdge;
import com.railwayteam.railways.util.MixinVariables;
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
        //String className = Thread.currentThread().getStackTrace()[3].getClassName();
        if (MixinVariables.signalPropagatorCallDepth > 0)//(className.equals("com.simibubi.create.content.trains.signal.SignalPropagator"))
            return;
        if (MixinVariables.temporarilySkipSwitches)
            return;
        TrackEdge relevantEdge = MixinVariables.trackEdgeTemporarilyFlipped ? ((TrackEdge) (Object) this) : other;
        MixinVariables.trackEdgeTemporarilyFlipped = false;
        // trains should be able to navigate through automatic switches
        if (MixinVariables.navigationCallDepth > 0
                && ISwitchDisabledEdge.isAutomatic(relevantEdge))
            return;
        if (MixinVariables.trackEdgeCarriageTravelling) { // don't switch, just allow through. Actual switching is handled by MixinTravellingPoint
            if (ISwitchDisabledEdge.isAutomatic(relevantEdge) && ISwitchDisabledEdge.isDisabled(relevantEdge)) {
//                ISwitchDisabledEdge.automaticallySelect(relevantEdge);
                return;
            }
        }
        if (ISwitchDisabledEdge.isDisabled(relevantEdge))// || ISwitchDisabledEdge.isDisabled((TrackEdge) (Object) this))
            cir.setReturnValue(false);
    }
}
