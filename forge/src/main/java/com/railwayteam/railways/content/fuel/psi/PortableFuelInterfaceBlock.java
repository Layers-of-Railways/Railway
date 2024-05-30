/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.content.fuel.psi;

import com.railwayteam.railways.mixin.AccessorPortableStorageInterfaceBlockEntity;
import com.railwayteam.railways.registry.forge.CRBlockEntitiesImpl;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllShapes;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.WrenchableDirectionalBlock;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PortableFuelInterfaceBlock extends WrenchableDirectionalBlock implements IBE<PortableFuelInterfaceBlockEntity> {
    public PortableFuelInterfaceBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos,
                                boolean isMoving) {
        withBlockEntityDo(world, pos, PortableFuelInterfaceBlockEntity::neighbourChanged);
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        AdvancementBehaviour.setPlacedBy(pLevel, pPos, pPlacer);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getNearestLookingDirection();
        if (context.getPlayer() != null && context.getPlayer()
                .isSteppingCarefully())
            direction = direction.getOpposite();
        return defaultBlockState().setValue(FACING, direction.getOpposite());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return AllShapes.PORTABLE_STORAGE_INTERFACE.get(state.getValue(FACING));
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
        return getBlockEntityOptional(worldIn, pos).map(be -> ((AccessorPortableStorageInterfaceBlockEntity) be).railways$isConnected() ? 15 : 0)
                .orElse(0);
    }

    @Override
    public Class<PortableFuelInterfaceBlockEntity> getBlockEntityClass() {
        return PortableFuelInterfaceBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends PortableFuelInterfaceBlockEntity> getBlockEntityType() {
        return CRBlockEntitiesImpl.PORTABLE_FUEL_INTERFACE.get();
    }
}
