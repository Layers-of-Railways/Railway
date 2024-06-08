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

package com.railwayteam.railways.content.buffer;

import com.railwayteam.railways.registry.CRBlockEntities;
import com.railwayteam.railways.util.AdventureUtils;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class WoodVariantTrackBufferBlock extends TrackBufferBlock<WoodVariantTrackBufferBlockEntity> {
    protected WoodVariantTrackBufferBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public Class<WoodVariantTrackBufferBlockEntity> getBlockEntityClass() {
        return WoodVariantTrackBufferBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends WoodVariantTrackBufferBlockEntity> getBlockEntityType() {
        return CRBlockEntities.TRACK_BUFFER_WOOD_VARIANT.get();
    }

    @SuppressWarnings("deprecation")
    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand,
                                 BlockHitResult pHit) {
        if (AdventureUtils.isAdventure(pPlayer))
            return InteractionResult.PASS;
        InteractionResult result = onBlockEntityUse(pLevel, pPos, be -> be.applyMaterialIfValid(pPlayer.getItemInHand(pHand)));
        if (result.consumesAction())
            return result;
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }
}
