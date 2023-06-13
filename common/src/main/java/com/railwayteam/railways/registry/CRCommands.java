package com.railwayteam.railways.registry;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
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
          .then(TrackDemoCommand.register());
    }

    LiteralCommandNode<CommandSourceStack> railwaysRoot = dispatcher.register(railwaysCommand);

    CommandNode<CommandSourceStack> snr = dispatcher.findNode(Collections.singleton("snr"));
    if (snr != null)
      return;

    dispatcher.getRoot()
        .addChild(AllCommands.buildRedirect("snr", railwaysRoot));
  }
}
