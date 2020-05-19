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
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.text.TranslationTextComponent;

public class TeamArgument implements ArgumentType<String> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "123");
   private static final DynamicCommandExceptionType TEAM_NOT_FOUND = new DynamicCommandExceptionType((p_208680_0_) -> {
      return new TranslationTextComponent("team.notFound", p_208680_0_);
   });

   public static TeamArgument team() {
      return new TeamArgument();
   }

   public static ScorePlayerTeam getTeam(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
      String s = context.getArgument(name, String.class);
      Scoreboard scoreboard = context.getSource().getServer().getScoreboard();
      ScorePlayerTeam scoreplayerteam = scoreboard.getTeam(s);
      if (scoreplayerteam == null) {
         throw TEAM_NOT_FOUND.create(s);
      } else {
         return scoreplayerteam;
      }
   }

   public String parse(StringReader p_parse_1_) throws CommandSyntaxException {
      return p_parse_1_.readUnquotedString();
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      return p_listSuggestions_1_.getSource() instanceof ISuggestionProvider ? ISuggestionProvider.suggest(((ISuggestionProvider)p_listSuggestions_1_.getSource()).getTeamNames(), p_listSuggestions_2_) : Suggestions.empty();
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}