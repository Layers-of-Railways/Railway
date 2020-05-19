package net.minecraft.test;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.StringTextComponent;

public class TestTypeArgument implements ArgumentType<String> {
   private static final Collection<String> field_229610_a_ = Arrays.asList("techtests", "mobtests");

   public String parse(StringReader p_parse_1_) throws CommandSyntaxException {
      String s = p_parse_1_.readUnquotedString();
      if (TestRegistry.func_229534_b_(s)) {
         return s;
      } else {
         Message message = new StringTextComponent("No such test class: " + s);
         throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
      }
   }

   public static TestTypeArgument func_229611_a_() {
      return new TestTypeArgument();
   }

   public static String func_229612_a_(CommandContext<CommandSource> p_229612_0_, String p_229612_1_) {
      return p_229612_0_.getArgument(p_229612_1_, String.class);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      return ISuggestionProvider.suggest(TestRegistry.func_229533_b_().stream(), p_listSuggestions_2_);
   }

   public Collection<String> getExamples() {
      return field_229610_a_;
   }
}