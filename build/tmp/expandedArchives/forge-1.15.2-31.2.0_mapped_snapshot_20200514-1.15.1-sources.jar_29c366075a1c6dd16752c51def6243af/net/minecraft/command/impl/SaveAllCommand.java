package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TranslationTextComponent;

public class SaveAllCommand {
   private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.save.failed"));

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("save-all").requires((p_198615_0_) -> {
         return p_198615_0_.hasPermissionLevel(4);
      }).executes((p_198610_0_) -> {
         return saveAll(p_198610_0_.getSource(), false);
      }).then(Commands.literal("flush").executes((p_198613_0_) -> {
         return saveAll(p_198613_0_.getSource(), true);
      })));
   }

   private static int saveAll(CommandSource source, boolean flush) throws CommandSyntaxException {
      source.sendFeedback(new TranslationTextComponent("commands.save.saving"), false);
      MinecraftServer minecraftserver = source.getServer();
      minecraftserver.getPlayerList().saveAllPlayerData();
      boolean flag = minecraftserver.save(true, flush, true);
      if (!flag) {
         throw FAILED_EXCEPTION.create();
      } else {
         source.sendFeedback(new TranslationTextComponent("commands.save.success"), true);
         return 1;
      }
   }
}