package com.railwayteam.railways.mixin;

import com.railwayteam.railways.content.extended_sliding_doors.SlidingDoorMode;
import com.railwayteam.railways.mixin_interfaces.ISidedStation;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlock;
import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorMovementBehaviour;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.station.GlobalStation;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SlidingDoorMovementBehaviour.class, remap = false)
public class MixinSlidingDoorMovementBehaviour {
    private SlidingDoorMode mode(MovementContext context) {
        return SlidingDoorMode.fromNbt(context.blockEntityData);
    }

    @Inject(method = "shouldUpdate", at = @At("HEAD"), cancellable = true)
    private void cancelManualUpdate(MovementContext context, boolean shouldOpen, CallbackInfoReturnable<Boolean> cir) {
        if (context.state != null && context.state.hasProperty(SlidingDoorBlock.HALF) && context.state.getValue(SlidingDoorBlock.HALF) == DoubleBlockHalf.UPPER) {
            cir.setReturnValue(false);
            return;
        }
        if (mode(context) == SlidingDoorMode.MANUAL)
            cir.setReturnValue(false);
    }

    @Inject(method = "shouldOpen", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    private void openOnlyCorrectSide(MovementContext context, CallbackInfoReturnable<Boolean> cir) {
        SlidingDoorMode mode = mode(context);
        if (mode.stationBased) {
            if (context.contraption.entity instanceof CarriageContraptionEntity cce) {
                Train train = cce.getCarriage().train;
                GlobalStation station = train.getCurrentStation();
                if (station == null) {
                    cir.setReturnValue(false);
                    return;
                } else {
                    if (train.currentlyBackwards) {
                        mode = mode.flipped();
                    }
                    if (!((((ISidedStation) station).opensLeft() && mode == SlidingDoorMode.STATION_LEFT) ||
                        (((ISidedStation) station).opensRight() && mode == SlidingDoorMode.STATION_RIGHT))) {
                        cir.setReturnValue(false);
                        return;
                    }
                }
            } else {
                cir.setReturnValue(false);
                return;
            }
        }
    }
}
