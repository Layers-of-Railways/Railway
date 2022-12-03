package com.railwayteam.railways.registry;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.railwayteam.railways.registry.commands.ClearCasingCacheCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;

import java.util.Collections;
import java.util.function.Predicate;

import static com.simibubi.create.foundation.command.AllCommands.buildRedirect;

public class CRCommands {
  public static final Predicate<CommandSourceStack> SOURCE_IS_PLAYER = cs -> cs.getEntity() instanceof Player;

  public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

//    LiteralCommandNode<CommandSourceStack> util = buildUtilityCommands();

    LiteralCommandNode<CommandSourceStack> railwaysRoot = dispatcher.register(Commands.literal("railways")
        .requires(cs -> cs.hasPermission(0))
        // general purpose
        .then(ClearCasingCacheCommand.register())

        // utility
//        .then(util)
    );

//    railwaysRoot.addChild(buildRedirect("u", util));

    CommandNode<CommandSourceStack> snr = dispatcher.findNode(Collections.singleton("snr"));
    if (snr != null)
      return;

    dispatcher.getRoot()
        .addChild(buildRedirect("snr", railwaysRoot));

  }

/*  private static LiteralCommandNode<CommandSourceStack> buildUtilityCommands() {

    return Commands.literal("util")
        .then(ReplaceInCommandBlocksCommand.register())
        .then(ClearBufferCacheCommand.register())
        .then(CameraDistanceCommand.register())
        .then(CameraAngleCommand.register())
        .then(FlySpeedCommand.register())
        //.then(KillTPSCommand.register())
        .build();

  }*/
}
