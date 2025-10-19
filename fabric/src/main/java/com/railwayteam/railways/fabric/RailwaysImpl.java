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

package com.railwayteam.railways.fabric;

import com.mojang.brigadier.CommandDispatcher;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.config.fabric.CRConfigsImpl;
import com.railwayteam.railways.content.fuel.tank.FuelTankBlock;
import com.railwayteam.railways.fabric.events.CommonEventsFabric;
import com.railwayteam.railways.registry.fabric.CRBlockEntitiesImpl;
import com.railwayteam.railways.registry.fabric.CRBlocksImpl;
import com.railwayteam.railways.registry.fabric.CRMountedStorageTypesImpl;
import com.railwayteam.railways.registry.fabric.CRParticleTypesParticleEntryImpl;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.api.contraption.BlockMovementChecks;
import com.simibubi.create.api.contraption.BlockMovementChecks.CheckResult;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.BiConsumer;

public class RailwaysImpl implements ModInitializer {
	@Override
	public void onInitialize() {
		Railways.init();
		CRConfigsImpl.register();
		CRParticleTypesParticleEntryImpl.register();
		CommonEventsFabric.init();
	}

	public static void finalizeRegistrate() {
		Railways.registrate().register();
	}

	public static void registerCommands(BiConsumer<CommandDispatcher<CommandSourceStack>, Boolean> consumer) {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> consumer.accept(dispatcher, environment.includeDedicated));
	}

	public static void platformBasedRegistration() {
		BlockMovementChecks.registerAttachedCheck((BlockState state, Level world, BlockPos pos, Direction direction) -> {
			if (state.getBlock() instanceof FuelTankBlock && ConnectivityHandler.isConnected(world, pos, pos.relative(direction)))
				return CheckResult.SUCCESS;
			return CheckResult.PASS;
		});

		CRMountedStorageTypesImpl.init();
		CRBlocksImpl.init();
		CRBlockEntitiesImpl.init();
	}
}
