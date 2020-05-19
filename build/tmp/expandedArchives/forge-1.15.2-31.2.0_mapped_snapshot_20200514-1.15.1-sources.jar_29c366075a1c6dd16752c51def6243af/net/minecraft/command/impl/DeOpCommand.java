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

public class DeOpCommand {
   private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.deop.failed"));

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("deop").requires((p_198325_0_) -> {
         return p_198325_0_.hasPermissionLevel(3);
      }).then(Commands.argument("targets", GameProfileArgument.gameProfile()).suggests((p_198323_0_, p_198323_1_) -> {
         return ISuggestionProvider.suggest(p_198323_0_.getSource().getServer().getPlayerList().getOppedPlayerNames(), p_198323_1_);
      }).executes((p_198324_0_) -> {
         return deopPlayers(p_198324_0_.getSource(), GameProfileArgument.getGameProfiles(p_198324_0_, "targets"));
      })));
   }

   private static int deopPlayers(CommandSource source, Collection<GameProfile> players) throws CommandSyntaxException {
      PlayerList playerlist = source.getServer().getPlayerList();
      int i = 0;

      for(GameProfile gameprofile : players) {
         if (playerlist.canSendCommands(gameprofile)) {
            playerlist.removeOp(gameprofile);
            ++i;
            source.sendFeedback(new TranslationTextComponent("commands.deop.success", players.iterator().next().getName()), true);
         }
      }

      if (i == 0) {
         throw FAILED_EXCEPTION.create();
      } else {
         source.getServer().kickPlayersNotWhitelisted(source);
         return i;
      }
   }
}