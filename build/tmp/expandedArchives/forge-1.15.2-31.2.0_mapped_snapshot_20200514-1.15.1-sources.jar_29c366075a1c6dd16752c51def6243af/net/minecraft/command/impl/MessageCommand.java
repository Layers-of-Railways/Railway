package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class MessageCommand {
   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      LiteralCommandNode<CommandSource> literalcommandnode = dispatcher.register(Commands.literal("msg").then(Commands.argument("targets", EntityArgument.players()).then(Commands.argument("message", MessageArgument.message()).executes((p_198539_0_) -> {
         return sendPrivateMessage(p_198539_0_.getSource(), EntityArgument.getPlayers(p_198539_0_, "targets"), MessageArgument.getMessage(p_198539_0_, "message"));
      }))));
      dispatcher.register(Commands.literal("tell").redirect(literalcommandnode));
      dispatcher.register(Commands.literal("w").redirect(literalcommandnode));
   }

   private static int sendPrivateMessage(CommandSource source, Collection<ServerPlayerEntity> recipients, ITextComponent message) {
      for(ServerPlayerEntity serverplayerentity : recipients) {
         serverplayerentity.sendMessage((new TranslationTextComponent("commands.message.display.incoming", source.getDisplayName(), message.deepCopy())).applyTextStyles(new TextFormatting[]{TextFormatting.GRAY, TextFormatting.ITALIC}));
         source.sendFeedback((new TranslationTextComponent("commands.message.display.outgoing", serverplayerentity.getDisplayName(), message.deepCopy())).applyTextStyles(new TextFormatting[]{TextFormatting.GRAY, TextFormatting.ITALIC}), false);
      }

      return recipients.size();
   }
}