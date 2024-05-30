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

package com.railwayteam.railways.fabric;

import com.mojang.brigadier.CommandDispatcher;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.config.fabric.CRConfigsImpl;
import com.railwayteam.railways.content.fuel.LiquidFuelManager;
import com.railwayteam.railways.fabric.events.CommonEventsFabric;
import com.railwayteam.railways.registry.fabric.CRParticleTypesParticleEntryImpl;
import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.util.function.BiConsumer;

public class RailwaysImpl implements ModInitializer {
	@Override
	public void onInitialize() {
		Railways.init();
		CRConfigsImpl.register();
		CRParticleTypesParticleEntryImpl.register();
		CommonEventsFabric.init();

		// This is fine at runtime, there's a mixin implementing that interface
		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener((IdentifiableResourceReloadListener) LiquidFuelManager.ReloadListener.INSTANCE);
	}

	public static String findVersion() {
		return FabricLoader.getInstance()
				.getModContainer(Railways.MODID)
				.orElseThrow()
				.getMetadata()
				.getVersion()
				.getFriendlyString();
	}

	public static void finalizeRegistrate() {
		Railways.registrate().register();
	}

	public static void registerCommands(BiConsumer<CommandDispatcher<CommandSourceStack>, Boolean> consumer) {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> consumer.accept(dispatcher, environment.includeDedicated));
	}

	public static void registerConfig(ModConfig.Type type, ForgeConfigSpec spec) {
		ForgeConfigRegistry.INSTANCE.register(
			Railways.MODID, type, spec
		);
//		ModLoadingContext.registerConfig(Railways.MODID, type, spec);
	}
}
