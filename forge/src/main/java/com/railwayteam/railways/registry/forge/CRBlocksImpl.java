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

package com.railwayteam.railways.registry.forge;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.fuel.psi.PortableFuelInterfaceBlock;
import com.railwayteam.railways.content.fuel.tank.FuelTankBlock;
import com.railwayteam.railways.content.fuel.tank.FuelTankItem;
import com.railwayteam.railways.content.fuel.tank.FuelTankModel;
import com.simibubi.create.AllTags;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.contraptions.BlockMovementChecks;
import com.simibubi.create.content.contraptions.actors.psi.PortableStorageInterfaceMovement;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

import static com.simibubi.create.AllMovementBehaviours.movementBehaviour;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

public class CRBlocksImpl {
    private static final CreateRegistrate REGISTRATE = Railways.registrate();

    @SuppressWarnings("removal")
    public static final BlockEntry<FuelTankBlock> FUEL_TANK = REGISTRATE.block("fuel_tank", FuelTankBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(BlockBehaviour.Properties::noOcclusion)
            .properties(p -> p.isRedstoneConductor((p1, p2, p3) -> true))
            .transform(pickaxeOnly())
            //.blockstate(new FuelTankGenerator()::generate) Handled by fabric subproject
            .onRegister(CreateRegistrate.blockModel(() -> FuelTankModel::standard))
            .addLayer(() -> RenderType::cutoutMipped)
            .item(FuelTankItem::new)
            .model(AssetLookup.customBlockItemModel("_", "block_single_window"))
            .build()
            .register();

    public static final BlockEntry<PortableFuelInterfaceBlock> PORTABLE_FUEL_INTERFACE = REGISTRATE.block("portable_fuel_interface", PortableFuelInterfaceBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.mapColor(MapColor.TERRACOTTA_LIGHT_GRAY))
            .transform(axeOrPickaxe())
            .blockstate((c, p) -> p.directionalBlock(c.get(), AssetLookup.partialBaseModel(c, p)))
            .onRegister(movementBehaviour(new PortableStorageInterfaceMovement()))
            .item()
            .tag(AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag)
            .transform(customItemModel())
            .register();

    public static void platformBasedRegistration() {
        BlockMovementChecks.registerAttachedCheck((BlockState state, Level world, BlockPos pos, Direction direction) -> {
            if (state.getBlock() instanceof FuelTankBlock && ConnectivityHandler.isConnected(world, pos, pos.relative(direction)))
                return BlockMovementChecks.CheckResult.SUCCESS;
            return BlockMovementChecks.CheckResult.PASS;
        });
    }
}
