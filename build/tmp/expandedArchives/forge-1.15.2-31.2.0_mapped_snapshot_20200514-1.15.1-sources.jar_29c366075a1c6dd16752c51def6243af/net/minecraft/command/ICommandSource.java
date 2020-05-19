package net.minecraft.command;

import net.minecraft.util.text.ITextComponent;

public interface ICommandSource {
   /** A CommandSource that ignores all messages. */
   ICommandSource DUMMY = new ICommandSource() {
      /**
       * Send a chat message to the CommandSender
       */
      public void sendMessage(ITextComponent component) {
      }

      public boolean shouldReceiveFeedback() {
         return false;
      }

      public boolean shouldReceiveErrors() {
         return false;
      }

      public boolean allowLogging() {
         return false;
      }
   };

   /**
    * Send a chat message to the CommandSender
    */
   void sendMessage(ITextComponent component);

   boolean shouldReceiveFeedback();

   boolean shouldReceiveErrors();

   boolean allowLogging();
}