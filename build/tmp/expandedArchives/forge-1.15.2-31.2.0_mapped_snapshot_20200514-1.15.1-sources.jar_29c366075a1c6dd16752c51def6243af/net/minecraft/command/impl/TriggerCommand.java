package net.minecraft.command.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ObjectiveArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.text.TranslationTextComponent;

public class TriggerCommand {
   private static final SimpleCommandExceptionType NOT_PRIMED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.trigger.failed.unprimed"));
   private static final SimpleCommandExceptionType NOT_A_TRIGGER = new SimpleCommandExceptionType(new TranslationTextComponent("commands.trigger.failed.invalid"));

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("trigger").then(Commands.argument("objective", ObjectiveArgument.objective()).suggests((p_198853_0_, p_198853_1_) -> {
         return suggestTriggers(p_198853_0_.getSource(), p_198853_1_);
      }).executes((p_198854_0_) -> {
         return incrementTrigger(p_198854_0_.getSource(), checkValidTrigger(p_198854_0_.getSource().asPlayer(), ObjectiveArgument.getObjective(p_198854_0_, "objective")));
      }).then(Commands.literal("add").then(Commands.argument("value", IntegerArgumentType.integer()).executes((p_198849_0_) -> {
         return addToTrigger(p_198849_0_.getSource(), checkValidTrigger(p_198849_0_.getSource().asPlayer(), ObjectiveArgument.getObjective(p_198849_0_, "objective")), IntegerArgumentType.getInteger(p_198849_0_, "value"));
      }))).then(Commands.literal("set").then(Commands.argument("value", IntegerArgumentType.integer()).executes((p_198855_0_) -> {
         return setTrigger(p_198855_0_.getSource(), checkValidTrigger(p_198855_0_.getSource().asPlayer(), ObjectiveArgument.getObjective(p_198855_0_, "objective")), IntegerArgumentType.getInteger(p_198855_0_, "value"));
      })))));
   }

   public static CompletableFuture<Suggestions> suggestTriggers(CommandSource source, SuggestionsBuilder builder) {
      Entity entity = source.getEntity();
      List<String> list = Lists.newArrayList();
      if (entity != null) {
         Scoreboard scoreboard = source.getServer().getScoreboard();
         String s = entity.getScoreboardName();

         for(ScoreObjective scoreobjective : scoreboard.getScoreObjectives()) {
            if (scoreobjective.getCriteria() == ScoreCriteria.TRIGGER && scoreboard.entityHasObjective(s, scoreobjective)) {
               Score score = scoreboard.getOrCreateScore(s, scoreobjective);
               if (!score.isLocked()) {
                  list.add(scoreobjective.getName());
               }
            }
         }
      }

      return ISuggestionProvider.suggest(list, builder);
   }

   private static int addToTrigger(CommandSource source, Score objective, int amount) {
      objective.increaseScore(amount);
      source.sendFeedback(new TranslationTextComponent("commands.trigger.add.success", objective.getObjective().func_197890_e(), amount), true);
      return objective.getScorePoints();
   }

   private static int setTrigger(CommandSource source, Score objective, int value) {
      objective.setScorePoints(value);
      source.sendFeedback(new TranslationTextComponent("commands.trigger.set.success", objective.getObjective().func_197890_e(), value), true);
      return value;
   }

   private static int incrementTrigger(CommandSource source, Score objectives) {
      objectives.increaseScore(1);
      source.sendFeedback(new TranslationTextComponent("commands.trigger.simple.success", objectives.getObjective().func_197890_e()), true);
      return objectives.getScorePoints();
   }

   private static Score checkValidTrigger(ServerPlayerEntity player, ScoreObjective objective) throws CommandSyntaxException {
      if (objective.getCriteria() != ScoreCriteria.TRIGGER) {
         throw NOT_A_TRIGGER.create();
      } else {
         Scoreboard scoreboard = player.getWorldScoreboard();
         String s = player.getScoreboardName();
         if (!scoreboard.entityHasObjective(s, objective)) {
            throw NOT_PRIMED.create();
         } else {
            Score score = scoreboard.getOrCreateScore(s, objective);
            if (score.isLocked()) {
               throw NOT_PRIMED.create();
            } else {
               score.setLocked(true);
               return score;
            }
         }
      }
   }
}