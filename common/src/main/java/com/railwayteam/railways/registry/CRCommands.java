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

package com.railwayteam.railways.registry;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.railwayteam.railways.registry.commands.ConductorDemoCommand;
import com.railwayteam.railways.registry.commands.PalettesDemoCommand;
import com.railwayteam.railways.registry.commands.ReloadCasingCollisionCommand;
import com.railwayteam.railways.registry.commands.ReloadCreativeTabsCommand;
import com.railwayteam.railways.registry.commands.SplitTrainCommand;
import com.railwayteam.railways.registry.commands.TrackDemoCommand;
import com.railwayteam.railways.registry.commands.TrainInfoCommand;
import com.railwayteam.railways.util.Utils;
import com.simibubi.create.infrastructure.command.AllCommands;
import net.minecraft.commands.CommandSourceStack;

import java.util.Collections;

import static net.minecraft.commands.Commands.literal;

public class CRCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, boolean dedicated) {

        var railwaysCommand = literal("railways")
                .requires(cs -> cs.hasPermission(0))
                //.then(ClearCasingCacheCommand.register())
                .then(SplitTrainCommand.register())
                .then(TrainInfoCommand.register());

        if (Utils.isDevEnv()) {
            railwaysCommand = railwaysCommand
                    .then(TrackDemoCommand.register())
                    .then(ConductorDemoCommand.register())
                    .then(ReloadCasingCollisionCommand.register())
                    .then(ReloadCreativeTabsCommand.register())
                    .then(PalettesDemoCommand.register());
        }

        LiteralCommandNode<CommandSourceStack> railwaysRoot = dispatcher.register(railwaysCommand);

        CommandNode<CommandSourceStack> snr = dispatcher.findNode(Collections.singleton("snr"));
        if (snr != null)
            return;

        dispatcher.getRoot()
                .addChild(AllCommands.buildRedirect("snr", railwaysRoot));
    }
}
