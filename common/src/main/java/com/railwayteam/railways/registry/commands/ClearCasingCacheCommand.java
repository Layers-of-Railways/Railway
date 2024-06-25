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

package com.railwayteam.railways.registry.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.railwayteam.railways.content.custom_tracks.casing.CasingRenderUtils;
import com.railwayteam.railways.multiloader.Env;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.commands.SharedSuggestionProvider;

import static com.railwayteam.railways.multiloader.ClientCommands.literal;
import static com.railwayteam.railways.multiloader.ClientCommands.sendSuccess;

public class ClearCasingCacheCommand {
  public static ArgumentBuilder<SharedSuggestionProvider, ?> register() {
    return literal("clear_casing_cache")
        .requires(cs -> cs.hasPermission(0))
        .executes(ctx -> {
          Env.CLIENT.runIfCurrent(() -> CasingRenderUtils::clearModelCache);

          sendSuccess(ctx.getSource(), Components.literal("cleared casing cache"));
          return 1;
        });
  }
}
