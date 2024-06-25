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

package com.railwayteam.railways.multiloader;

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
