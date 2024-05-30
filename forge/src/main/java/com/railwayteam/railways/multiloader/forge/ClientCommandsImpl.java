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

package com.railwayteam.railways.multiloader.forge;

import com.railwayteam.railways.Railways;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.ClientCommandSourceStack;

public class ClientCommandsImpl {
	public static void sendSuccess(SharedSuggestionProvider provider, Component text) {
		if (provider instanceof ClientCommandSourceStack forge)
			forge.sendSuccess(() -> text, true);
		else Railways.LOGGER.error("Invalid command source: " + provider);
	}

	public static void sendFailure(SharedSuggestionProvider provider, Component text) {
		if (provider instanceof ClientCommandSourceStack forge)
			forge.sendFailure(text);
		else Railways.LOGGER.error("Invalid command source: " + provider);
	}
}
