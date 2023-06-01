/*package com.railwayteam.railways.mixin;

import com.railwayteam.railways.mixin_interfaces.IBogeyTypeAwareTravellingPoint;
import com.simibubi.create.content.trains.IBogeyBlock;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageBogey;
import com.simibubi.create.content.trains.entity.TravellingPoint;
import com.simibubi.create.foundation.utility.Couple;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CarriageBogey.class, remap = false)
public class MixinCarriageBogey {
    @Shadow
    Couple<TravellingPoint> points;

    @Shadow
    IBogeyBlock type;

    @Shadow public Carriage carriage;

    @Inject(method = {"leading", "trailing"}, at = @At("HEAD"))
    private void setTravellingPointTypes(CallbackInfoReturnable<TravellingPoint> cir) {
        points.forEach(point -> ((IBogeyTypeAwareTravellingPoint) point).setType(((AccessorCarriageBogey) carriage.leadingBogey()).getType())); //TODO bogey api
    }
}
*/