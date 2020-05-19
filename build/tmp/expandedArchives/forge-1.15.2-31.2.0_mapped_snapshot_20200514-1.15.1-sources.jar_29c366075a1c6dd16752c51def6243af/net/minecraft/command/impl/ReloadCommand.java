package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TranslationTextComponent;

public class ReloadCommand {
   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("reload").requires((p_198599_0_) -> {
         return p_198599_0_.hasPermissionLevel(2);
      }).executes((p_198598_0_) -> {
         p_198598_0_.getSource().sendFeedback(new TranslationTextComponent("commands.reload.success"), true);
         p_198598_0_.getSource().getServer().reload();
         return 0;
      }));
   }
}