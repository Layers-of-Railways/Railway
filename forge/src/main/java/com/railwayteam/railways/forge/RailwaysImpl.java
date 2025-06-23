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

package com.railwayteam.railways.forge;

import com.mojang.brigadier.CommandDispatcher;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.config.forge.CRConfigsImpl;
import com.railwayteam.railways.content.fuel.tank.FuelTankBlock;
import com.railwayteam.railways.multiloader.Env;
import com.railwayteam.railways.registry.forge.CRBlockEntitiesImpl;
import com.railwayteam.railways.registry.forge.CRBlocksImpl;
import com.railwayteam.railways.registry.forge.CRCreativeModeTabsImpl;
import com.railwayteam.railways.registry.forge.CRMountedStorageTypesImpl;
import com.railwayteam.railways.registry.forge.CRParticleTypesParticleEntryImpl;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.api.contraption.BlockMovementChecks;
import com.simibubi.create.api.contraption.BlockMovementChecks.CheckResult;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands.CommandSelection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

@Mod(Railways.MOD_ID)
@Mod.EventBusSubscriber
public class RailwaysImpl {
	static IEventBus bus;

	public RailwaysImpl() {
		bus = FMLJavaModLoadingContext.get().getModEventBus();
		CRCreativeModeTabsImpl.register(RailwaysImpl.bus);
		Railways.init();
		CRConfigsImpl.register(ModLoadingContext.get());
		CRParticleTypesParticleEntryImpl.register(bus);
		//noinspection Convert2MethodRef
		Env.CLIENT.runIfCurrent(() -> () -> RailwaysClientImpl.init());
	}

	public static void finalizeRegistrate() {
		Railways.registrate().registerEventListeners(bus);
	}

	private static final Set<BiConsumer<CommandDispatcher<CommandSourceStack>, Boolean>> commandConsumers = new HashSet<>();

	public static void registerCommands(BiConsumer<CommandDispatcher<CommandSourceStack>, Boolean> consumer) {
		commandConsumers.add(consumer);
	}

	@SubscribeEvent
	public static void onCommandRegistration(RegisterCommandsEvent event) {
		CommandSelection selection = event.getCommandSelection();
		boolean dedicated = selection == CommandSelection.ALL || selection == CommandSelection.DEDICATED;
		commandConsumers.forEach(consumer -> consumer.accept(event.getDispatcher(), dedicated));
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
