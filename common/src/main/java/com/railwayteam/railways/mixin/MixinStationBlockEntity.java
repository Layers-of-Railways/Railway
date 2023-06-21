package com.railwayteam.railways.mixin;

import com.railwayteam.railways.content.custom_bogeys.selection_menu.BogeyCategoryHandlerServer;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import com.simibubi.create.content.trains.track.ITrackBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = StationBlockEntity.class, remap = false)
public class MixinStationBlockEntity {
    @Inject(method = "trackClicked", at = @At("HEAD"))
    private void storePlayer(Player player, InteractionHand hand, ITrackBlock track, BlockState state, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        BogeyCategoryHandlerServer.currentPlayer = player.getUUID();
    }

    @Inject(method = "trackClicked", at = @At("RETURN"))
    private void clearPlayer(Player player, InteractionHand hand, ITrackBlock track, BlockState state, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        BogeyCategoryHandlerServer.currentPlayer = null;
    }
}
