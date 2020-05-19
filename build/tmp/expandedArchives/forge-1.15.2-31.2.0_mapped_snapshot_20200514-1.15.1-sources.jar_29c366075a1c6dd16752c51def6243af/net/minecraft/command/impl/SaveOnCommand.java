package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

public class SaveOnCommand {
   private static final SimpleCommandExceptionType SAVE_ALREADY_ON_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.save.alreadyOn"));

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("save-on").requires((p_198623_0_) -> {
         return p_198623_0_.hasPermissionLevel(4);
      }).executes((p_198622_0_) -> {
         CommandSource commandsource = p_198622_0_.getSource();
         boolean flag = false;

         for(ServerWorld serverworld : commandsource.getServer().getWorlds()) {
            if (serverworld != null && serverworld.disableLevelSaving) {
               serverworld.disableLevelSaving = false;
               flag = true;
            }
         }

         if (!flag) {
            throw SAVE_ALREADY_ON_EXCEPTION.create();
         } else {
            commandsource.sendFeedback(new TranslationTextComponent("commands.save.enabled"), true);
            return 1;
         }
      }));
   }
}