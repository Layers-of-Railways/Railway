package com.railwayteam.railways.mixin;

import com.railwayteam.railways.content.extended_sliding_doors.SlidingDoorMode;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.behaviour.DoorMovingInteraction;
import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = DoorMovingInteraction.class, remap = false)
public class MixinDoorMovingInteraction {
    /*
    prevent players from just opening special doors unless sneaking
     */
    @Inject(
        method = "handle",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;", ordinal = 1, remap = true),
        cancellable = true
    )
    private void snr$lockSpecial(Player player, Contraption contraption, BlockPos pos,
                                 BlockState currentState, CallbackInfoReturnable<BlockState> cir) {
        if (!(currentState.getBlock() instanceof SlidingDoorBlock)) return;
        boolean lower = currentState.getValue(SlidingDoorBlock.HALF) == DoubleBlockHalf.LOWER;
        StructureTemplate.StructureBlockInfo info = contraption.getBlocks().get(lower ? pos : pos.below());
        if (info != null && SlidingDoorMode.fromNbt(info.nbt) == SlidingDoorMode.SPECIAL && !player.isShiftKeyDown())
            cir.setReturnValue(currentState);
    }
}
