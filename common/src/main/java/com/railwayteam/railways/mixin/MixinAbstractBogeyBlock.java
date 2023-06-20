package com.railwayteam.railways.mixin;

import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlockEntity;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = AbstractBogeyBlock.class, remap = false)
public abstract class MixinAbstractBogeyBlock {
    @Shadow protected abstract BlockState copyProperties(BlockState source, BlockState target);

    // fixme this is a Create bug, file a report (styles not placing the correct block)
    @Inject(method = "use",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;displayClientMessage(Lnet/minecraft/network/chat/Component;Z)V", remap = true, ordinal = 0),
            locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void placeCorrectedBlock(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                     BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir, ItemStack stack,
                                     BlockEntity be, AbstractBogeyBlockEntity sbte, BogeyStyle currentStyle,
                                     BogeySizes.BogeySize size, BogeyStyle style) {
        if (state.getBlock() != style.getBlockOfSize(size)) {
            // need to place block
            CompoundTag oldData = sbte.getBogeyData();

            BlockState targetState = style.getBlockOfSize(size).defaultBlockState();
            targetState = copyProperties(state, targetState);
            level.setBlock(pos, targetState, 3);

            BlockEntity newBlockEntity = level.getBlockEntity(pos);
            if (!(newBlockEntity instanceof AbstractBogeyBlockEntity newTileEntity)) {
                cir.setReturnValue(InteractionResult.FAIL);
                return;
            }
            newTileEntity.setBogeyData(oldData);
        }
    }
}
