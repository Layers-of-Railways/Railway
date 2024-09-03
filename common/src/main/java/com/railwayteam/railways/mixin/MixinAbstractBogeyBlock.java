/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
import java.util.List;
import java.util.Map;

@Mixin(value = AbstractBogeyBlock.class, remap = false)
public abstract class MixinAbstractBogeyBlock {
    @Shadow
    protected abstract BlockState copyProperties(BlockState source, BlockState target);

    @Shadow
    public abstract BlockState getStateOfSize(AbstractBogeyBlockEntity sbte, BogeySizes.BogeySize size);

    // fixme this is a Create bug, file a report (styles not placing the correct block)
    // Has been merged, awaiting release
    @Inject(method = "use",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;displayClientMessage(Lnet/minecraft/network/chat/Component;Z)V", remap = true, ordinal = 0),
            locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true, remap = true)
    private void placeCorrectedBlock(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                     BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir, ItemStack stack,
                                     BlockEntity be, AbstractBogeyBlockEntity sbbe, BogeyStyle currentStyle,
                                     BogeySizes.BogeySize size, BogeyStyle style) {
        if (state.getBlock() != style.getBlockOfSize(size)) {
            CompoundTag oldData = sbbe.getBogeyData();
            level.setBlock(pos, copyProperties(state, getStateOfSize(sbbe, size)), 3);
            BlockEntity newBlockEntity = level.getBlockEntity(pos);
            if (!(newBlockEntity instanceof AbstractBogeyBlockEntity newBlockEntity1)) {
                cir.setReturnValue(InteractionResult.FAIL);
                return;
            }
            newBlockEntity1.setBogeyData(oldData);
        }
    }

    @Unique
    private final ThreadLocal<TrackType> railways$trackType = new ThreadLocal<>();

    @Inject(method = "getNextStyle(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Lcom/simibubi/create/content/trains/bogey/BogeyStyle;", at = @At("HEAD"), remap = true)
    private void storeSupportType(Level level, BlockPos pos, CallbackInfoReturnable<BogeyStyle> cir) {
        AbstractBogeyBlock<?> $this = (AbstractBogeyBlock<?>) (Object) this;
        BlockPos trackPos = $this.isUpsideDown(level.getBlockState(pos)) ? pos.above() : pos.below();
        if (level.getBlockState(trackPos).getBlock() instanceof ITrackBlock trackBlock) {
            railways$trackType.set(trackBlock.getMaterial().trackType);
        } else {
            railways$trackType.remove();
        }
    }

    @Inject(method = "getNextStyle(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Lcom/simibubi/create/content/trains/bogey/BogeyStyle;", at = @At("RETURN"), remap = true)
    private void clearSupportType(Level level, BlockPos pos, CallbackInfoReturnable<BogeyStyle> cir) {
        railways$trackType.remove();
    }

    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/bogey/AbstractBogeyBlock;getNextStyle(Lcom/simibubi/create/content/trains/bogey/BogeyStyle;)Lcom/simibubi/create/content/trains/bogey/BogeyStyle;"), remap = true)
    private void storeSupportTypeUse(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                     BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        AbstractBogeyBlock<?> $this = (AbstractBogeyBlock<?>) (Object) this;
        BlockPos trackPos = $this.isUpsideDown(state) ? pos.above() : pos.below();
        if (level.getBlockState(trackPos).getBlock() instanceof ITrackBlock trackBlock) {
            railways$trackType.set(trackBlock.getMaterial().trackType);
        } else {
            railways$trackType.remove();
        }
    }

    @Inject(method = "use", at = @At("RETURN"), remap = true)
    private void clearSupportTypeUse(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                     BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        railways$trackType.remove();
    }

    @WrapOperation(method = "getNextStyle(Lcom/simibubi/create/content/trains/bogey/BogeyStyle;)Lcom/simibubi/create/content/trains/bogey/BogeyStyle;", at = @At(value = "INVOKE", target = "Ljava/util/Map;values()Ljava/util/Collection;"))
    private Collection<BogeyStyle> filterStyles(Map<ResourceLocation, BogeyStyle> instance, Operation<Collection<BogeyStyle>> original) {
        TrackType trackType = railways$trackType.get();
        if (trackType == null) {
            return original.call(instance);
        } else {
            return original.call(instance).stream().filter((style) -> CRBogeyStyles.styleFitsTrack(style, trackType)).toList();
        }
    }

    @WrapOperation(method = "getNextStyle(Lcom/simibubi/create/content/trains/bogey/BogeyStyle;)Lcom/simibubi/create/content/trains/bogey/BogeyStyle;", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/utility/Iterate;cycleValue(Ljava/util/List;Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object wrapCycleWithFallback(List<Object> list, Object style, Operation<Object> original) {
        try {
            return original.call(list, style);
        } catch (IllegalArgumentException e) {
            return list.get(0);
        }
    }
}
