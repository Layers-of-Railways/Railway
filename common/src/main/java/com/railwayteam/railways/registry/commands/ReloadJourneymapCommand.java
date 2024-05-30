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

package com.railwayteam.railways.registry.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.railwayteam.railways.compat.Mods;
import com.railwayteam.railways.compat.journeymap.DummyRailwayMarkerHandler;
import com.railwayteam.railways.multiloader.Env;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.commands.SharedSuggestionProvider;

import static com.railwayteam.railways.multiloader.ClientCommands.*;

public class ReloadJourneymapCommand {
    public static ArgumentBuilder<SharedSuggestionProvider, ?> register() {
        return literal("reload_jmap")
            .requires(cs -> cs.hasPermission(0))
            .executes(ctx -> {
                SharedSuggestionProvider source = ctx.getSource();
                if (Mods.JOURNEYMAP.isLoaded) {
                    Env.CLIENT.runIfCurrent(() -> () -> DummyRailwayMarkerHandler.getInstance().reloadMarkers());

                    sendSuccess(source, Components.literal("Reloaded journeymap"));
                    return 1;
                } else {
                    sendFailure(source, Components.literal("Journeymap not loaded"));
                    return 0;
                }
            });
    }
}
