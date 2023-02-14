package com.railwayteam.railways.multiloader.commands.fabric;

import com.railwayteam.railways.Railways;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

public class ClientCommandsImpl {
	public static void sendSuccess(SharedSuggestionProvider provider, Component text) {
		if (provider instanceof FabricClientCommandSource fabric)
			fabric.sendFeedback(text);
		else Railways.LOGGER.error("Invalid command source: " + provider);
	}

	public static void sendFailure(SharedSuggestionProvider provider, Component text) {
		if (provider instanceof FabricClientCommandSource fabric)
			fabric.sendError(text);
		else Railways.LOGGER.error("Invalid command source: " + provider);
	}
}
