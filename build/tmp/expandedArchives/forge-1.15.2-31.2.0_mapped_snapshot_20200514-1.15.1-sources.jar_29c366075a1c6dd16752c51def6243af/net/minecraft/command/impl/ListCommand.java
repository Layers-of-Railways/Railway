package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import java.util.List;
import java.util.function.Function;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TranslationTextComponent;

public class ListCommand {
   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("list").executes((p_198523_0_) -> {
         return listNames(p_198523_0_.getSource());
      }).then(Commands.literal("uuids").executes((p_208202_0_) -> {
         return listUUIDs(p_208202_0_.getSource());
      })));
   }

   private static int listNames(CommandSource source) {
      return listPlayers(source, PlayerEntity::getDisplayName);
   }

   private static int listUUIDs(CommandSource source) {
      return listPlayers(source, PlayerEntity::getDisplayNameAndUUID);
   }

   private static int listPlayers(CommandSource source, Function<ServerPlayerEntity, ITextComponent> nameExtractor) {
      PlayerList playerlist = source.getServer().getPlayerList();
      List<ServerPlayerEntity> list = playerlist.getPlayers();
      ITextComponent itextcomponent = TextComponentUtils.makeList(list, nameExtractor);
      source.sendFeedback(new TranslationTextComponent("commands.list.players", list.size(), playerlist.getMaxPlayers(), itextcomponent), false);
      return list.size();
   }
}