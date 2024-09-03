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

import com.railwayteam.railways.content.bogey_menu.handler.BogeyMenuHandlerServer;
import com.railwayteam.railways.content.custom_tracks.CustomTrackBlock;
import com.railwayteam.railways.content.custom_tracks.casing.CasingCollisionUtils;
import com.railwayteam.railways.content.custom_tracks.monorail.MonorailTrackBlock;
import com.railwayteam.railways.content.roller_extensions.TrackReplacePaver;
import com.railwayteam.railways.registry.CRBogeyStyles;
import com.railwayteam.railways.registry.CRShapes;
import com.simibubi.create.AllBogeyStyles;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.bogey.BogeySizes.BogeySize;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackBlockEntity;
import com.simibubi.create.content.trains.track.TrackMaterial.TrackType;
import com.simibubi.create.content.trains.track.TrackShape;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(value = TrackBlock.class, remap = false)
public class MixinTrackBlock {
    @Inject(method = "use", at = @At("HEAD"), cancellable = true, remap = true)
    private void extendedUse(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        //noinspection ConstantValue
        if (!(((Object) this) instanceof MonorailTrackBlock)) {
            InteractionResult result = CustomTrackBlock.casingUse(state, world, pos, player, hand, hit);
            if (result != null) {
                cir.setReturnValue(result);
            }
        }
    }

    @Inject(method = "getBogeyAnchor", at = @At("HEAD"), cancellable = true)
    private void placeCustomStyle(BlockGetter world, BlockPos pos, BlockState state, CallbackInfoReturnable<BlockState> cir) {
        if (BogeyMenuHandlerServer.getCurrentPlayer() == null)
            return;
        Pair<BogeyStyle, BogeySize> styleData = BogeyMenuHandlerServer.getStyle(BogeyMenuHandlerServer.getCurrentPlayer());
        BogeyStyle style = styleData.getFirst();

        TrackType trackType = ((TrackBlock) (Object) this).getMaterial().trackType;

        Optional<BogeyStyle> mappedStyleOptional = CRBogeyStyles.getMapped(style, trackType, true);
        if (mappedStyleOptional.isPresent())
            style = mappedStyleOptional.get();

        BogeySize selectedSize = styleData.getSecond();
        if (style == AllBogeyStyles.STANDARD)
            return;

        BogeySize size = selectedSize != null ? selectedSize : BogeySizes.getAllSizesSmallToLarge().get(0);
        int escape = BogeySizes.getAllSizesSmallToLarge().size();
        while (!style.validSizes().contains(size)) {
            if (escape < 0)
                return;
            size = size.increment();
            escape--;
        }
        Block block = style.getBlockOfSize(size);
        cir.setReturnValue(
                block.defaultBlockState()
                        .setValue(BlockStateProperties.HORIZONTAL_AXIS,
                                state.getValue(TrackBlock.SHAPE) == TrackShape.XO ? Direction.Axis.X : Direction.Axis.Z)
        );
    }

    @Redirect(method = "onPlace", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;I)V", remap = true), remap = true)
    private void maybeMakeTickInstant(Level instance, BlockPos blockPos, Block block, int i) {
        if (TrackReplacePaver.tickInstantly)
            i = 0;
        instance.scheduleTick(blockPos, block, i);
    }

    @Inject(method = "getCollisionShape", at = @At("HEAD"), cancellable = true, remap = true)
    private void casingCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext, CallbackInfoReturnable<VoxelShape> cir) {
        if (pLevel.getBlockEntity(pPos) instanceof TrackBlockEntity tbe) {
            if (CasingCollisionUtils.shouldMakeCollision(tbe, pState)) {
                cir.setReturnValue(CRShapes.BOTTOM_SLAB);
            }
        }
    }

    @Inject(method = "onRemove", at = @At("HEAD"), remap = true)
    private void removeCasingCollisions(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving, CallbackInfo ci) {
        if (pLevel.getBlockEntity(pPos) instanceof TrackBlockEntity tbe) {
            CasingCollisionUtils.manageTracks(tbe, true);
        }
    }
}
