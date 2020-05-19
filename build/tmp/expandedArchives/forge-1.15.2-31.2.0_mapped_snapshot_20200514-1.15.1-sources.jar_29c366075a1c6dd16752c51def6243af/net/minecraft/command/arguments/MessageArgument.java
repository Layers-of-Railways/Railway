package net.minecraft.command.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class MessageArgument implements ArgumentType<MessageArgument.Message> {
   private static final Collection<String> EXAMPLES = Arrays.asList("Hello world!", "foo", "@e", "Hello @p :)");

   public static MessageArgument message() {
      return new MessageArgument();
   }

   public static ITextComponent getMessage(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
      return context.getArgument(name, MessageArgument.Message.class).toComponent(context.getSource(), context.getSource().hasPermissionLevel(2));
   }

   public MessageArgument.Message parse(StringReader p_parse_1_) throws CommandSyntaxException {
      return MessageArgument.Message.parse(p_parse_1_, true);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   public static class Message {
      private final String text;
      private final MessageArgument.Part[] selectors;

      public Message(String textIn, MessageArgument.Part[] selectorsIn) {
         this.text = textIn;
         this.selectors = selectorsIn;
      }

      /**
       * Converts this message into a text component, replacing any selectors in the text with the actual evaluated
       * selector.
       */
      public ITextComponent toComponent(CommandSource source, boolean allowSelectors) throws CommandSyntaxException {
         if (this.selectors.length != 0 && allowSelectors) {
            ITextComponent itextcomponent = new StringTextComponent(this.text.substring(0, this.selectors[0].getStart()));
            int i = this.selectors[0].getStart();

            for(MessageArgument.Part messageargument$part : this.selectors) {
               ITextComponent itextcomponent1 = messageargument$part.toComponent(source);
               if (i < messageargument$part.getStart()) {
                  itextcomponent.appendText(this.text.substring(i, messageargument$part.getStart()));
               }

               if (itextcomponent1 != null) {
                  itextcomponent.appendSibling(itextcomponent1);
               }

               i = messageargument$part.getEnd();
            }

            if (i < this.text.length()) {
               itextcomponent.appendText(this.text.substring(i, this.text.length()));
            }

            return itextcomponent;
         } else {
            return new StringTextComponent(this.text);
         }
      }

      /**
       * Parses a message. The algorithm for this is simply to run though and look for selectors, ignoring any invalid
       * selectors in the text (since players may type e.g. "[@]").
       */
      public static MessageArgument.Message parse(StringReader reader, boolean allowSelectors) throws CommandSyntaxException {
         String s = reader.getString().substring(reader.getCursor(), reader.getTotalLength());
         if (!allowSelectors) {
            reader.setCursor(reader.getTotalLength());
            return new MessageArgument.Message(s, new MessageArgument.Part[0]);
         } else {
            List<MessageArgument.Part> list = Lists.newArrayList();
            int i = reader.getCursor();

            while(true) {
               int j;
               EntitySelector entityselector;
               while(true) {
                  if (!reader.canRead()) {
                     return new MessageArgument.Message(s, list.toArray(new MessageArgument.Part[list.size()]));
                  }

                  if (reader.peek() == '@') {
                     j = reader.getCursor();

                     try {
                        EntitySelectorParser entityselectorparser = new EntitySelectorParser(reader);
                        entityselector = entityselectorparser.parse();
                        break;
                     } catch (CommandSyntaxException commandsyntaxexception) {
                        if (commandsyntaxexception.getType() != EntitySelectorParser.SELECTOR_TYPE_MISSING && commandsyntaxexception.getType() != EntitySelectorParser.UNKNOWN_SELECTOR_TYPE) {
                           throw commandsyntaxexception;
                        }

                        reader.setCursor(j + 1);
                     }
                  } else {
                     reader.skip();
                  }
               }

               list.add(new MessageArgument.Part(j - i, reader.getCursor() - i, entityselector));
            }
         }
      }
   }

   public static class Part {
      private final int start;
      private final int end;
      private final EntitySelector selector;

      public Part(int startIn, int endIn, EntitySelector selectorIn) {
         this.start = startIn;
         this.end = endIn;
         this.selector = selectorIn;
      }

      public int getStart() {
         return this.start;
      }

      public int getEnd() {
         return this.end;
      }

      /**
       * Runs the selector and returns the component produced by it. This method does not actually appear to ever return
       * null.
       */
      @Nullable
      public ITextComponent toComponent(CommandSource source) throws CommandSyntaxException {
         return EntitySelector.joinNames(this.selector.select(source));
      }
   }
}