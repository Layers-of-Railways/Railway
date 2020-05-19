package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

public class SaveOffCommand {
   private static final SimpleCommandExceptionType SAVE_ALREADY_OFF_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.save.alreadyOff"));

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("save-off").requires((p_198619_0_) -> {
         return p_198619_0_.hasPermissionLevel(4);
      }).executes((p_198618_0_) -> {
         CommandSource commandsource = p_198618_0_.getSource();
         boolean flag = false;

         for(ServerWorld serverworld : commandsource.getServer().getWorlds()) {
            if (serverworld != null && !serverworld.disableLevelSaving) {
               serverworld.disableLevelSaving = true;
               flag = true;
            }
         }

         if (!flag) {
            throw SAVE_ALREADY_OFF_EXCEPTION.create();
         } else {
            commandsource.sendFeedback(new TranslationTextComponent("commands.save.disabled"), true);
            return 1;
         }
      }));
   }
}