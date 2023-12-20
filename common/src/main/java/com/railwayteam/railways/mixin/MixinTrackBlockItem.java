package com.railwayteam.railways.mixin;

import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.TrackBlockItem;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TrackBlockItem.class)
public class MixinTrackBlockItem {
    @Inject(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"), cancellable = true)
    private void noStartingFromGenericCrossings(UseOnContext pContext, CallbackInfoReturnable<InteractionResult> cir) {
        BlockPos pos = pContext.getClickedPos();
        Level level = pContext.getLevel();
        BlockState state = level.getBlockState(pos);
        Player player = pContext.getPlayer();

        if (state.getBlock() instanceof ITrackBlock track && track.getTrackAxes(level, pos, state)
            .size() > 1) {
            if (!level.isClientSide)
                player.displayClientMessage(Lang.translateDirect("track.junction_start")
                    .withStyle(ChatFormatting.RED), true);
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}
