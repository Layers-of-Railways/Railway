package com.railwayteam.railways.mixin;

import com.railwayteam.railways.content.roller_extensions.TrackReplacePaver;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.contraptions.actors.roller.PaveTask;
import com.simibubi.create.content.contraptions.actors.roller.RollerMovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.trains.track.ITrackBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(value = RollerMovementBehaviour.class, remap = false)
public abstract class MixinRollerMovementBehaviour {
    @Shadow protected abstract BlockState getStateToPaveWithAsSlab(MovementContext context);

    @Shadow protected abstract BlockState getStateToPaveWith(MovementContext context);


    @Shadow @Nullable protected abstract PaveTask createHeightProfileForTracks(MovementContext context);

    // fixme this is a Create bug
    // https://github.com/Creators-of-Create/Create/pull/6272 Remove this mixin whenever this is merged
    // Has been merged, awaiting release
    @Deprecated
    @Inject(method = "canBreak", at = @At("RETURN"), cancellable = true)
    private void noBreakingTracks(Level world, BlockPos breakingPos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (AllTags.AllBlockTags.TRACKS.matches(state))
            cir.setReturnValue(false);
    }

    @Inject(method = "triggerPaver", at = @At("HEAD"), cancellable = true)
    private void skipTracksAndPaveTracks(MovementContext context, BlockPos pos, CallbackInfo ci) {
        BlockState stateToPaveWith = getStateToPaveWith(context);
        BlockState stateToPaveWithAsSlab = getStateToPaveWithAsSlab(context);
        int mode = context.blockEntityData.getInt("ScrollValue");
        if (mode == 3) { // TRACK_REPLACE
            ci.cancel();
            TrackReplacePaver.pave(context, pos, stateToPaveWith, createHeightProfileForTracks(context));
        } else if (stateToPaveWith.getBlock() instanceof ITrackBlock) {
            ci.cancel();
        }
    }
}
