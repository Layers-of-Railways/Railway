package com.railwayteam.railways.multiloader.forge;

import com.railwayteam.railways.Railways;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.ClientCommandSourceStack;

import java.util.function.Supplier;

public class ClientCommandsImpl {
	// FIXME POSSIBLE JANK REMOVE SUPPLIER<> IF IT DOESNT WORK LEAVE COMPONENT
	public static void sendSuccess(SharedSuggestionProvider provider, Supplier<Component> text) {
		if (provider instanceof ClientCommandSourceStack forge)
			forge.sendSuccess(text, true);
		else Railways.LOGGER.error("Invalid command source: " + provider);
	}

	public static void sendFailure(SharedSuggestionProvider provider, Component text) {
		if (provider instanceof ClientCommandSourceStack forge)
			forge.sendFailure(text);
		else Railways.LOGGER.error("Invalid command source: " + provider);
	}
}
