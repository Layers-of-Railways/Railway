package net.minecraft.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.util.text.ITextComponent;

public class CommandException extends RuntimeException {
   private final ITextComponent component;

   public CommandException(ITextComponent message) {
      super(message.getUnformattedComponentText(), (Throwable)null, CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES, CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES);
      this.component = message;
   }

   public ITextComponent getComponent() {
      return this.component;
   }
}