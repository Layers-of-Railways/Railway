package com.railwayteam.railways.mixin;

import com.railwayteam.railways.content.extended_sliding_doors.SlidingDoorMode;
import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlock;
import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

// For forge only, it is necessary to call ValueSettingsInputHandler.onBlockActivated at the head of SlidingDoorBlock's stopItQuark method - this is implemented in a forge-specific mixin
@Mixin(value = SlidingDoorBlock.class, remap = false)
public abstract class MixinSlidingDoorBlock {
    @Inject(method = "neighborChanged",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;setValue(Lnet/minecraft/world/level/block/state/properties/Property;Ljava/lang/Comparable;)Ljava/lang/Object;", ordinal = 0, remap = true),
        remap = true, locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void snr$preventManualDoorRedstoneReaction(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock,
                                                       BlockPos pFromPos, boolean pIsMoving, CallbackInfo ci,
                                                       boolean lower, boolean isPowered, SlidingDoorBlockEntity be) {
        if (be == null)
            return;
        if (((SlidingDoorMode.IHasDoorMode) be).snr$getSlidingDoorMode() == SlidingDoorMode.MANUAL) {
            ci.cancel();
        }
    }

    @Inject(method = "use", at = @At("HEAD"), remap = true, cancellable = true)
    private void snr$preventSpecialDoorManualOpen(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer,
                                                  InteractionHand pHand, BlockHitResult pHit,
                                                  CallbackInfoReturnable<InteractionResult> cir) {
        boolean lower = pState.getValue(SlidingDoorBlock.HALF) == DoubleBlockHalf.LOWER;
        BlockEntity be = pLevel.getBlockEntity(lower ? pPos : pPos.below());
        if (be instanceof SlidingDoorMode.IHasDoorMode doorMode && doorMode.snr$getSlidingDoorMode() == SlidingDoorMode.SPECIAL)
            cir.setReturnValue(InteractionResult.FAIL);
    }

    @Inject(method = "setOpen", at = @At("HEAD"), cancellable = true)
    private void preventSpecialDoorEntityOpen(@Nullable Entity entity, Level level, BlockState state, BlockPos pos, boolean _open, CallbackInfo ci) {
        if (entity != null) {
            boolean lower = state.getValue(SlidingDoorBlock.HALF) == DoubleBlockHalf.LOWER;
            BlockEntity be = level.getBlockEntity(lower ? pos : pos.below());
            if (be instanceof SlidingDoorMode.IHasDoorMode doorMode && doorMode.snr$getSlidingDoorMode() == SlidingDoorMode.SPECIAL)
                ci.cancel();
        }
    }
}
