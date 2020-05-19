package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;

public class GameModeCommand {
   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      LiteralArgumentBuilder<CommandSource> literalargumentbuilder = Commands.literal("gamemode").requires((p_198485_0_) -> {
         return p_198485_0_.hasPermissionLevel(2);
      });

      for(GameType gametype : GameType.values()) {
         if (gametype != GameType.NOT_SET) {
            literalargumentbuilder.then(Commands.literal(gametype.getName()).executes((p_198483_1_) -> {
               return setGameMode(p_198483_1_, Collections.singleton(p_198483_1_.getSource().asPlayer()), gametype);
            }).then(Commands.argument("target", EntityArgument.players()).executes((p_198486_1_) -> {
               return setGameMode(p_198486_1_, EntityArgument.getPlayers(p_198486_1_, "target"), gametype);
            })));
         }
      }

      dispatcher.register(literalargumentbuilder);
   }

   private static void sendGameModeFeedback(CommandSource source, ServerPlayerEntity player, GameType gameTypeIn) {
      ITextComponent itextcomponent = new TranslationTextComponent("gameMode." + gameTypeIn.getName());
      if (source.getEntity() == player) {
         source.sendFeedback(new TranslationTextComponent("commands.gamemode.success.self", itextcomponent), true);
      } else {
         if (source.getWorld().getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK)) {
            player.sendMessage(new TranslationTextComponent("gameMode.changed", itextcomponent));
         }

         source.sendFeedback(new TranslationTextComponent("commands.gamemode.success.other", player.getDisplayName(), itextcomponent), true);
      }

   }

   private static int setGameMode(CommandContext<CommandSource> source, Collection<ServerPlayerEntity> players, GameType gameTypeIn) {
      int i = 0;

      for(ServerPlayerEntity serverplayerentity : players) {
         if (serverplayerentity.interactionManager.getGameType() != gameTypeIn) {
            serverplayerentity.setGameType(gameTypeIn);
            sendGameModeFeedback(source.getSource(), serverplayerentity, gameTypeIn);
            ++i;
         }
      }

      return i;
   }
}