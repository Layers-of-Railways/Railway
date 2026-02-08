/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2026 The Railways Team
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

package com.railwayteam.railways.content.smokestack.block;

import com.railwayteam.railways.content.smokestack.RotationType;
import com.railwayteam.railways.content.smokestack.SmokeEmissionParams;
import com.railwayteam.railways.content.smokestack.block.be.SmokeStackBlockEntity;
import com.railwayteam.railways.registry.CRBlockEntities;
import com.railwayteam.railways.util.ShapeWrapper;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SmokeStackBlock extends AbstractSmokeStackBlock<SmokeStackBlockEntity> {
    public final SmokeEmissionParams emissionParams;
    public final RotationType rotationType;
    public final boolean createsStationarySmoke;

    // because createBlockStateDefinition is called from the super constructor, and it needs a rotation type
    private static final ThreadLocal<RotationType> definitionRotationType = new ThreadLocal<>();

    private static Properties setDefinitionRotationType(Properties properties, RotationType rotationType) {
        definitionRotationType.set(rotationType);
        return properties;
    }

    public SmokeStackBlock(Properties properties, RotationType rotationType, SmokeEmissionParams emissionParams, ShapeWrapper shape, boolean createsStationarySmoke) {
        super(setDefinitionRotationType(properties, rotationType), shape);
        definitionRotationType.remove();

        this.rotationType = rotationType;
        this.emissionParams = emissionParams;
        this.createsStationarySmoke = createsStationarySmoke;

        this.registerDefaultState(this.makeDefaultState());
    }

    protected RotationType getConstructSafeRotationType() {
        return rotationType != null ? rotationType : definitionRotationType.get();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        getConstructSafeRotationType().createBlockStateDefinition(builder);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state == null) return null;
        return rotationType.getStateForPlacement(context, state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rotation) {
        return rotationType.rotate(state, rotation);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror) {
        return rotationType.mirror(state, mirror);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return rotationType.getShape(pState, shape);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pPlayer.getItemInHand(pHand).getItem() instanceof DyeItem dyeItem) {
            DyeColor color = dyeItem.getDyeColor();
            withBlockEntityDo(pLevel, pPos, te -> te.setColor(color));
            if (!pPlayer.isCreative()) {
                pPlayer.getItemInHand(pHand).shrink(1);
            }
            return InteractionResult.sidedSuccess(pLevel.isClientSide);
        }
        if (pPlayer.getItemInHand(pHand).is(ItemTags.SOUL_FIRE_BASE_BLOCKS)) {
            withBlockEntityDo(pLevel, pPos, te -> te.setSoul(true));
            if (!pPlayer.isCreative()) {
                pPlayer.getItemInHand(pHand).shrink(1);
            }
            return InteractionResult.sidedSuccess(pLevel.isClientSide);
        }
        if (pPlayer.isShiftKeyDown()) {
            withBlockEntityDo(pLevel, pPos, te -> {
                te.setSoul(false);
                te.setColor(null);
            });
            return InteractionResult.sidedSuccess(pLevel.isClientSide);
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        IBE.onRemove(state, level, pos, newState);
    }

    @Override
    public Class<SmokeStackBlockEntity> getBlockEntityClass() {
        return SmokeStackBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends SmokeStackBlockEntity> getBlockEntityType() {
        return CRBlockEntities.SMOKE_STACK.get();
    }

    @Override
    public BlockState getRotatedBlockState(BlockState originalState, Direction targetedFace) {
        if (targetedFace.getAxis() != Direction.Axis.Y) targetedFace = Direction.UP;
        return super.getRotatedBlockState(originalState, targetedFace);
    }
}
