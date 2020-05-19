package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TranslationTextComponent;

public class MeCommand {
   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("me").then(Commands.argument("action", StringArgumentType.greedyString()).executes((p_198365_0_) -> {
         p_198365_0_.getSource().getServer().getPlayerList().sendMessage(new TranslationTextComponent("chat.type.emote", p_198365_0_.getSource().getDisplayName(), StringArgumentType.getString(p_198365_0_, "action")));
         return 1;
      })));
   }
}