package com.railwayteam.railways.mixin.client;

import com.railwayteam.railways.compat.journeymap.DummyRailwayMarkerHandler;
import com.simibubi.create.content.logistics.trains.entity.TrainPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(value = TrainPacket.class, remap = false)
public class MixinTrainPacket {
    @Shadow
    UUID trainId;

    @Inject(method = "lambda$handle$0", at = @At(value = "INVOKE", target = "Ljava/util/Map;remove(Ljava/lang/Object;)Ljava/lang/Object;"))
    private void catchRemoval(CallbackInfo ci) {
        if (DummyRailwayMarkerHandler.getInstance() != null) {
            DummyRailwayMarkerHandler.getInstance().removeTrain(trainId);
        }
    }
}
