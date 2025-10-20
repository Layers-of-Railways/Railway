/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2025 The Railways Team
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

package com.railwayteam.railways.registry.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.railwayteam.railways.multiloader.Env;
import com.railwayteam.railways.util.DevCapeUtils;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import static com.railwayteam.railways.multiloader.ClientCommands.literal;
import static com.railwayteam.railways.multiloader.ClientCommands.sendSuccess;

public class ReloadDevCapesCommand {
  public static ArgumentBuilder<SharedSuggestionProvider, ?> register() {
    return literal("reload_dev_capes")
        .requires(cs -> cs.hasPermission(0))
        .executes(ctx -> {
          Env.CLIENT.runIfCurrent(() -> DevCapeUtils.INSTANCE::refresh);

          sendSuccess(ctx.getSource(), Component.literal("Refreshed dev capes"));
          return 1;
        });
  }
}
