package com.railwayteam.railways.forge.mixin;

import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsInputHandler;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SlidingDoorBlock.class, remap = false)
public class SlidingDoorBlockMixin {
    /*
    Ensure that 'scroll' behaviours get a chance against anti-quarkification
     */
    @Inject(method = "stopItQuark",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraftforge/event/entity/player/PlayerInteractEvent$RightClickBlock;setCanceled(Z)V"
        ), cancellable = true)
    private static void stopItStopItQuark(PlayerInteractEvent.RightClickBlock event, CallbackInfo ci) {
        ValueSettingsInputHandler.onBlockActivated(event);

        if (event.isCanceled() || event.getResult() == Event.Result.DENY || event.getUseBlock() == Event.Result.DENY)
            ci.cancel();
    }
}
