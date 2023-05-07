package com.railwayteam.railways.mixin;

import com.railwayteam.railways.registry.CREdgePointTypes;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.signal.SignalBoundary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SignalBoundary.class, remap = false)
public class MixinSignalBoundary {
    @Inject(method = "canCoexistWith", at = @At("RETURN"), cancellable = true)
    private void railways$switchOrCouplerCanCoexist(EdgePointType<?> otherType, boolean front, CallbackInfoReturnable<Boolean> cir) {
        if (otherType == CREdgePointTypes.COUPLER || otherType == CREdgePointTypes.SWITCH)
            cir.setReturnValue(true);
    }
}
