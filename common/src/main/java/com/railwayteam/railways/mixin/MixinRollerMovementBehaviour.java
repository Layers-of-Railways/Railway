package com.railwayteam.railways.mixin;

import com.simibubi.create.AllTags;
import com.simibubi.create.content.contraptions.actors.roller.RollerMovementBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// fixme this is a Create bug
@Mixin(value = RollerMovementBehaviour.class, remap = false)
public class MixinRollerMovementBehaviour {
    @Inject(method = "canBreak", at = @At("RETURN"), cancellable = true)
    private void noBreakingTracks(Level world, BlockPos breakingPos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (AllTags.AllBlockTags.TRACKS.matches(state))
            cir.setReturnValue(false);
    }
}
