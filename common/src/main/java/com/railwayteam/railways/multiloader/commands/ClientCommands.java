package com.railwayteam.railways.multiloader.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

public class ClientCommands {
	public static LiteralArgumentBuilder<SharedSuggestionProvider> literal(String name) {
		return LiteralArgumentBuilder.literal(name);
	}

	public static <T> RequiredArgumentBuilder<SharedSuggestionProvider, T> argument(String name, ArgumentType<T> type) {
		return RequiredArgumentBuilder.argument(name, type);
	}

	@ExpectPlatform
	public static void sendSuccess(SharedSuggestionProvider provider, Component text) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static void sendFailure(SharedSuggestionProvider provider, Component text) {
		throw new AssertionError();
	}
}
