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

package com.railwayteam.railways.content.fuel.tank;

import com.railwayteam.railways.registry.fabric.CRBlockEntitiesImpl;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class FuelTankItem extends BlockItem {
    // fabric: (https://github.com/Fabricators-of-Create/Create/issues/690) because of ordering differences, we need to delay connection by a tick when placing multiblocks with NBT.
    // If the item has NBT, it needs to be applied to a controller. However, ordering is different on fabric.
    // on forge, the block is placed, the data is set, and the tanks connect.
    // on fabric, the block is placed, the tanks connect, and the data is set.
    // However, now that the tank is not a controller, nothing happens.
    // solution: hacky static state storage. If we're placing NBT, delay connection until next tick.
    @ApiStatus.Internal
    public static boolean IS_PLACING_NBT = false;

    public FuelTankItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public @NotNull InteractionResult place(@NotNull BlockPlaceContext ctx) {
        IS_PLACING_NBT = FuelTankItem.checkPlacingNbt(ctx);
        InteractionResult initialResult = super.place(ctx);
        IS_PLACING_NBT = false;
        if (!initialResult.consumesAction())
            return initialResult;
        tryMultiPlace(ctx);
        return initialResult;
    }

    @Override
    protected boolean updateCustomBlockEntityTag(@NotNull BlockPos blockPos, Level level, Player player,
                                                 @NotNull ItemStack itemStack, @NotNull BlockState blockState) {
        MinecraftServer minecraftserver = level.getServer();
        if (minecraftserver == null)
            return false;
        CompoundTag nbt = itemStack.getTagElement("BlockEntityTag");
        if (nbt != null) {
            nbt.remove("Luminosity");
            nbt.remove("Size");
            nbt.remove("Height");
            nbt.remove("Controller");
            nbt.remove("LastKnownPos");
            if (nbt.contains("TankContent")) {
                FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt.getCompound("TankContent"));
                if (!fluid.isEmpty()) {
                    fluid.setAmount(Math.min(FuelTankBlockEntity.getCapacityMultiplier(), fluid.getAmount()));
                    nbt.put("TankContent", fluid.writeToNBT(new CompoundTag()));
                }
            }
        }
        return super.updateCustomBlockEntityTag(blockPos, level, player, itemStack, blockState);
    }

    private void tryMultiPlace(BlockPlaceContext ctx) {
        Player player = ctx.getPlayer();
        if (player == null)
            return;
        if (player.isShiftKeyDown())
            return;
        Direction face = ctx.getClickedFace();
        if (!face.getAxis()
                .isVertical())
            return;
        ItemStack stack = ctx.getItemInHand();
        Level world = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockPos placedOnPos = pos.relative(face.getOpposite());
        BlockState placedOnState = world.getBlockState(placedOnPos);

        if (!FuelTankBlock.isTank(placedOnState))
            return;
        FuelTankBlockEntity tankAt = ConnectivityHandler.partAt(CRBlockEntitiesImpl.FUEL_TANK.get(), world, placedOnPos);
        if (tankAt == null)
            return;
        FuelTankBlockEntity controllerBE = tankAt.getControllerBE();
        if (controllerBE == null)
            return;

        int width = controllerBE.width;
        if (width == 1)
            return;

        int tanksToPlace = 0;
        BlockPos startPos = face == Direction.DOWN ? controllerBE.getBlockPos()
                .below()
                : controllerBE.getBlockPos()
                .above(controllerBE.height);

        if (startPos.getY() != pos.getY())
            return;

        for (int xOffset = 0; xOffset < width; xOffset++) {
            for (int zOffset = 0; zOffset < width; zOffset++) {
                BlockPos offsetPos = startPos.offset(xOffset, 0, zOffset);
                BlockState blockState = world.getBlockState(offsetPos);
                if (FuelTankBlock.isTank(blockState))
                    continue;
                if (!blockState.getMaterial()
                        .isReplaceable())
                    return;
                tanksToPlace++;
            }
        }

        if (!player.isCreative() && stack.getCount() < tanksToPlace)
            return;

        for (int xOffset = 0; xOffset < width; xOffset++) {
            for (int zOffset = 0; zOffset < width; zOffset++) {
                BlockPos offsetPos = startPos.offset(xOffset, 0, zOffset);
                BlockState blockState = world.getBlockState(offsetPos);
                if (FuelTankBlock.isTank(blockState))
                    continue;
                BlockPlaceContext context = BlockPlaceContext.at(ctx, offsetPos, face);
                player.getExtraCustomData()
                        .putBoolean("SilenceTankSound", true);
                IS_PLACING_NBT = checkPlacingNbt(context);
                super.place(context);
                IS_PLACING_NBT = false;
                player.getExtraCustomData()
                        .getBoolean("SilenceTankSound");
            }
        }
    }

    public static boolean checkPlacingNbt(BlockPlaceContext ctx) {
        ItemStack item = ctx.getItemInHand();
        return BlockItem.getBlockEntityData(item) != null;
    }
}