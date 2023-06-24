package com.railwayteam.railways.mixin;

import com.simibubi.create.content.contraptions.actors.roller.RollerBlockEntity;
import com.simibubi.create.content.contraptions.actors.roller.RollerMovementBehaviour;
import com.simibubi.create.content.trains.track.ITrackBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = RollerBlockEntity.class, remap = false)
public class MixinRollerBlockEntity {
    @Inject(method = "isValidMaterial", at = @At("HEAD"), cancellable = true)
    private void makeTracksValid(ItemStack newFilter, CallbackInfoReturnable<Boolean> cir) {
        if (newFilter.isEmpty())
            return;
        BlockState appliedState = RollerMovementBehaviour.getStateToPaveWith(newFilter);
        if (appliedState.getBlock() instanceof ITrackBlock)
            cir.setReturnValue(true);
    }
}
