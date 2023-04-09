package com.railwayteam.railways.registry;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.railwayteam.railways.registry.commands.SplitTrainCommand;
import com.railwayteam.railways.registry.commands.TrainInfoCommand;
import com.simibubi.create.foundation.command.AllCommands;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.Collections;

import static net.minecraft.commands.Commands.literal;

public class CRCommands {
  public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext, Commands.CommandSelection commandSelection) {

    LiteralCommandNode<CommandSourceStack> railwaysRoot = dispatcher.register(literal("railways")
        .requires(cs -> cs.hasPermission(0))
        //.then(ClearCasingCacheCommand.register())
        .then(SplitTrainCommand.register())
        .then(TrainInfoCommand.register())
    );

    CommandNode<CommandSourceStack> snr = dispatcher.findNode(Collections.singleton("snr"));
    if (snr != null)
      return;

    dispatcher.getRoot()
        .addChild(AllCommands.buildRedirect("snr", railwaysRoot));
  }
}
