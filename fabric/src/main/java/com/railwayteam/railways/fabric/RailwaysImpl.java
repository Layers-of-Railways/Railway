package com.railwayteam.railways.fabric;

import com.mojang.brigadier.CommandDispatcher;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.fabric.events.CommonEventsFabric;
import com.railwayteam.railways.registry.fabric.CRParticleTypesParticleEntryImpl;
import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.util.function.BiConsumer;

public class RailwaysImpl implements ModInitializer {
	@Override
	public void onInitialize() {
		Railways.init();
		CRParticleTypesParticleEntryImpl.register();
		CommonEventsFabric.init();
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
