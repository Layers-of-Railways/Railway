package com.railwayteam.railways.fabric;

import com.mojang.brigadier.CommandDispatcher;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.fabric.events.CommonEventsFabric;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands.CommandSelection;
import net.minecraftforge.api.ModLoadingContext;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.util.TriConsumer;

public class RailwaysImpl implements ModInitializer {
	@Override
	public void onInitialize() {
		Railways.init();
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

	public static void registerCommands(TriConsumer<CommandDispatcher<CommandSourceStack>, CommandBuildContext, CommandSelection> consumer) {
		CommandRegistrationCallback.EVENT.register(consumer::accept);
	}

	public static void registerConfig(ModConfig.Type type, ForgeConfigSpec spec) {
		ModLoadingContext.registerConfig(Railways.MODID, type, spec);
	}
}
