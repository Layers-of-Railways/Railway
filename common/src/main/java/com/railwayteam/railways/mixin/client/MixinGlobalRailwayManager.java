package com.railwayteam.railways.mixin.client;

import com.simibubi.create.content.trains.GlobalRailwayManager;
import com.simibubi.create.content.trains.entity.Train;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.UUID;

@Mixin(value = GlobalRailwayManager.class, remap = false)
public abstract class MixinGlobalRailwayManager {
    @Shadow public Map<UUID, Train> trains;

/*    @Inject(method = "clientTick", at = @At("HEAD"))
    private void showTrainDebug(CallbackInfo ci) {
        if (KineticDebugger.isActive())
            for (Train train : trains.values())
                TravellingPointVisualizer.debugTrain(train);
    }*/
}
