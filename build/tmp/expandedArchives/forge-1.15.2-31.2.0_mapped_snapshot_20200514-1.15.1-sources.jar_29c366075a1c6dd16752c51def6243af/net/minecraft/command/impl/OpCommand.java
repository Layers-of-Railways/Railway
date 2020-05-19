package net.minecraft.command.impl;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.GameProfileArgument;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.TranslationTextComponent;

public class OpCommand {
   private static final SimpleCommandExceptionType ALREADY_OP = new SimpleCommandExceptionType(new TranslationTextComponent("commands.op.failed"));

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("op").requires((p_198545_0_) -> {
         return p_198545_0_.hasPermissionLevel(3);
      }).then(Commands.argument("targets", GameProfileArgument.gameProfile()).suggests((p_198543_0_, p_198543_1_) -> {
         PlayerList playerlist = p_198543_0_.getSource().getServer().getPlayerList();
         return ISuggestionProvider.suggest(playerlist.getPlayers().stream().filter((p_198540_1_) -> {
            return !playerlist.canSendCommands(p_198540_1_.getGameProfile());
         }).map((p_200545_0_) -> {
            return p_200545_0_.getGameProfile().getName();
         }), p_198543_1_);
      }).executes((p_198544_0_) -> {
         return opPlayers(p_198544_0_.getSource(), GameProfileArgument.getGameProfiles(p_198544_0_, "targets"));
      })));
   }

   private static int opPlayers(CommandSource source, Collection<GameProfile> gameProfiles) throws CommandSyntaxException {
      PlayerList playerlist = source.getServer().getPlayerList();
      int i = 0;

      for(GameProfile gameprofile : gameProfiles) {
         if (!playerlist.canSendCommands(gameprofile)) {
            playerlist.addOp(gameprofile);
            ++i;
            source.sendFeedback(new TranslationTextComponent("commands.op.success", gameProfiles.iterator().next().getName()), true);
         }
      }

      if (i == 0) {
         throw ALREADY_OP.create();
      } else {
         return i;
      }
   }
}