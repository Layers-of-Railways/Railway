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
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.StringTextComponent;

public class TestArgArgument implements ArgumentType<TestFunctionInfo> {
   private static final Collection<String> field_229664_a_ = Arrays.asList("techtests.piston", "techtests");

   public TestFunctionInfo parse(StringReader p_parse_1_) throws CommandSyntaxException {
      String s = p_parse_1_.readUnquotedString();
      Optional<TestFunctionInfo> optional = TestRegistry.func_229537_d_(s);
      if (optional.isPresent()) {
         return optional.get();
      } else {
         Message message = new StringTextComponent("No such test: " + s);
         throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
      }
   }

   public static TestArgArgument func_229665_a_() {
      return new TestArgArgument();
   }

   public static TestFunctionInfo func_229666_a_(CommandContext<CommandSource> p_229666_0_, String p_229666_1_) {
      return p_229666_0_.getArgument(p_229666_1_, TestFunctionInfo.class);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      Stream<String> stream = TestRegistry.func_229529_a_().stream().map(TestFunctionInfo::func_229657_a_);
      return ISuggestionProvider.suggest(stream, p_listSuggestions_2_);
   }

   public Collection<String> getExamples() {
      return field_229664_a_;
   }
}