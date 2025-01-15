
/*
 * Steam 'n' Rails
 * Copyright (c) 2024-2025 The Railways Team
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
import com.railwayteam.railways.mixin.AccessorCreativeModeTabs;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class ReloadCreativeTabsCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("reload_creative_tabs")
            .requires(cs -> cs.hasPermission(2))
            .executes(ctx -> {
                AccessorCreativeModeTabs.setCACHED_PARAMETERS(null);
                ctx.getSource().sendSuccess(() -> Component.literal("Reloaded Creative Tabs"), true);
                return 1;
            });
    }
}
