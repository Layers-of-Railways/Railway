package com.railwayteam.railways.mixin;

import com.railwayteam.railways.content.extended_sliding_doors.SlidingDoorMode;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlock;
import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorMovementBehaviour;
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
    private void cancelUpdateForManual(MovementContext context, boolean shouldOpen, CallbackInfoReturnable<Boolean> cir) {
        if (context.state != null && context.state.hasProperty(SlidingDoorBlock.HALF) && context.state.getValue(SlidingDoorBlock.HALF) == DoubleBlockHalf.UPPER) {
            cir.setReturnValue(false);
            return;
        }
        if (mode(context) == SlidingDoorMode.MANUAL)
            cir.setReturnValue(false);
    }
}
