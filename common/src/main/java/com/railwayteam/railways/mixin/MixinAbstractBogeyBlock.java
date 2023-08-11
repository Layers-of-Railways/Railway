package com.railwayteam.railways.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.railwayteam.railways.registry.CRBogeyStyles;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlockEntity;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.TrackMaterial.TrackType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collection;
import java.util.Map;

@Mixin(value = AbstractBogeyBlock.class, remap = false)
public abstract class MixinAbstractBogeyBlock {
    @Shadow protected abstract BlockState copyProperties(BlockState source, BlockState target);

    // fixme this is a Create bug, file a report (styles not placing the correct block)
    @Inject(method = "use",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;displayClientMessage(Lnet/minecraft/network/chat/Component;Z)V", remap = true, ordinal = 0),
            locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true, remap = true)
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

    @Unique
    private final ThreadLocal<TrackType> snr$trackType = new ThreadLocal<>();

    @Inject(method = "getNextStyle(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Lcom/simibubi/create/content/trains/bogey/BogeyStyle;", at = @At("HEAD"))
    private void storeSupportType(Level level, BlockPos pos, CallbackInfoReturnable<BogeyStyle> cir) {
        AbstractBogeyBlock<?> $this = (AbstractBogeyBlock<?>) (Object) this;
        BlockPos trackPos = $this.isUpsideDown(level.getBlockState(pos)) ? pos.above() : pos.below();
        if (level.getBlockState(trackPos).getBlock() instanceof ITrackBlock trackBlock) {
            snr$trackType.set(trackBlock.getMaterial().trackType);
        } else {
            snr$trackType.remove();
        }
    }

    @Inject(method = "getNextStyle(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Lcom/simibubi/create/content/trains/bogey/BogeyStyle;", at = @At("RETURN"))
    private void clearSupportType(Level level, BlockPos pos, CallbackInfoReturnable<BogeyStyle> cir) {
        snr$trackType.remove();
    }

    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/bogey/AbstractBogeyBlock;getNextStyle(Lcom/simibubi/create/content/trains/bogey/BogeyStyle;)Lcom/simibubi/create/content/trains/bogey/BogeyStyle;"), remap = true)
    private void storeSupportTypeUse(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                     BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        AbstractBogeyBlock<?> $this = (AbstractBogeyBlock<?>) (Object) this;
        BlockPos trackPos = $this.isUpsideDown(state) ? pos.above() : pos.below();
        if (level.getBlockState(trackPos).getBlock() instanceof ITrackBlock trackBlock) {
            snr$trackType.set(trackBlock.getMaterial().trackType);
        } else {
            snr$trackType.remove();
        }
    }

    @Inject(method = "use", at = @At("RETURN"), remap = true)
    private void clearSupportTypeUse(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                     BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        snr$trackType.remove();
    }

    @WrapOperation(method = "getNextStyle(Lcom/simibubi/create/content/trains/bogey/BogeyStyle;)Lcom/simibubi/create/content/trains/bogey/BogeyStyle;", at = @At(value = "INVOKE", target = "Ljava/util/Map;values()Ljava/util/Collection;"))
    private Collection<BogeyStyle> filterStyles(Map<ResourceLocation, BogeyStyle> instance, Operation<Collection<BogeyStyle>> original) {
        TrackType trackType = snr$trackType.get();
        if (trackType == null) {
            return original.call(instance);
        } else {
            return original.call(instance).stream().filter((style) -> CRBogeyStyles.styleFitsTrack(style, trackType)).toList();
        }
    }
}
