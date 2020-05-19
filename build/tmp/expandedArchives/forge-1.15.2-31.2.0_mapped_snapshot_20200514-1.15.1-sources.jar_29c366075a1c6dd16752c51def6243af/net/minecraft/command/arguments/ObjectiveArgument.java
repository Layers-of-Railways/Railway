package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.text.TranslationTextComponent;

public class ObjectiveArgument implements ArgumentType<String> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "*", "012");
   private static final DynamicCommandExceptionType OBJECTIVE_NOT_FOUND = new DynamicCommandExceptionType((p_208671_0_) -> {
      return new TranslationTextComponent("arguments.objective.notFound", p_208671_0_);
   });
   private static final DynamicCommandExceptionType OBJECTIVE_READ_ONLY = new DynamicCommandExceptionType((p_208669_0_) -> {
      return new TranslationTextComponent("arguments.objective.readonly", p_208669_0_);
   });
   public static final DynamicCommandExceptionType OBJECTIVE_NAME_TOO_LONG = new DynamicCommandExceptionType((p_208670_0_) -> {
      return new TranslationTextComponent("commands.scoreboard.objectives.add.longName", p_208670_0_);
   });

   public static ObjectiveArgument objective() {
      return new ObjectiveArgument();
   }

   public static ScoreObjective getObjective(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
      String s = context.getArgument(name, String.class);
      Scoreboard scoreboard = context.getSource().getServer().getScoreboard();
      ScoreObjective scoreobjective = scoreboard.getObjective(s);
      if (scoreobjective == null) {
         throw OBJECTIVE_NOT_FOUND.create(s);
      } else {
         return scoreobjective;
      }
   }

   public static ScoreObjective getWritableObjective(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
      ScoreObjective scoreobjective = getObjective(context, name);
      if (scoreobjective.getCriteria().isReadOnly()) {
         throw OBJECTIVE_READ_ONLY.create(scoreobjective.getName());
      } else {
         return scoreobjective;
      }
   }

   public String parse(StringReader p_parse_1_) throws CommandSyntaxException {
      String s = p_parse_1_.readUnquotedString();
      if (s.length() > 16) {
         throw OBJECTIVE_NAME_TOO_LONG.create(16);
      } else {
         return s;
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      if (p_listSuggestions_1_.getSource() instanceof CommandSource) {
         return ISuggestionProvider.suggest(((CommandSource)p_listSuggestions_1_.getSource()).getServer().getScoreboard().func_197897_d(), p_listSuggestions_2_);
      } else if (p_listSuggestions_1_.getSource() instanceof ISuggestionProvider) {
         ISuggestionProvider isuggestionprovider = (ISuggestionProvider)p_listSuggestions_1_.getSource();
         return isuggestionprovider.getSuggestionsFromServer((CommandContext<ISuggestionProvider>)p_listSuggestions_1_, p_listSuggestions_2_);
      } else {
         return Suggestions.empty();
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}