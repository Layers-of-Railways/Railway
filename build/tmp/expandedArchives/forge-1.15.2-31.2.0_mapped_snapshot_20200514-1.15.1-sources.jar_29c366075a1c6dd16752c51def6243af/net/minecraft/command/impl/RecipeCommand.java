package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.text.TranslationTextComponent;

public class RecipeCommand {
   private static final SimpleCommandExceptionType GIVE_FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.recipe.give.failed"));
   private static final SimpleCommandExceptionType TAKE_FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.recipe.take.failed"));

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("recipe").requires((p_198593_0_) -> {
         return p_198593_0_.hasPermissionLevel(2);
      }).then(Commands.literal("give").then(Commands.argument("targets", EntityArgument.players()).then(Commands.argument("recipe", ResourceLocationArgument.resourceLocation()).suggests(SuggestionProviders.ALL_RECIPES).executes((p_198588_0_) -> {
         return giveRecipes(p_198588_0_.getSource(), EntityArgument.getPlayers(p_198588_0_, "targets"), Collections.singleton(ResourceLocationArgument.getRecipe(p_198588_0_, "recipe")));
      })).then(Commands.literal("*").executes((p_198591_0_) -> {
         return giveRecipes(p_198591_0_.getSource(), EntityArgument.getPlayers(p_198591_0_, "targets"), p_198591_0_.getSource().getServer().getRecipeManager().getRecipes());
      })))).then(Commands.literal("take").then(Commands.argument("targets", EntityArgument.players()).then(Commands.argument("recipe", ResourceLocationArgument.resourceLocation()).suggests(SuggestionProviders.ALL_RECIPES).executes((p_198587_0_) -> {
         return takeRecipes(p_198587_0_.getSource(), EntityArgument.getPlayers(p_198587_0_, "targets"), Collections.singleton(ResourceLocationArgument.getRecipe(p_198587_0_, "recipe")));
      })).then(Commands.literal("*").executes((p_198592_0_) -> {
         return takeRecipes(p_198592_0_.getSource(), EntityArgument.getPlayers(p_198592_0_, "targets"), p_198592_0_.getSource().getServer().getRecipeManager().getRecipes());
      })))));
   }

   private static int giveRecipes(CommandSource source, Collection<ServerPlayerEntity> targets, Collection<IRecipe<?>> recipes) throws CommandSyntaxException {
      int i = 0;

      for(ServerPlayerEntity serverplayerentity : targets) {
         i += serverplayerentity.unlockRecipes(recipes);
      }

      if (i == 0) {
         throw GIVE_FAILED_EXCEPTION.create();
      } else {
         if (targets.size() == 1) {
            source.sendFeedback(new TranslationTextComponent("commands.recipe.give.success.single", recipes.size(), targets.iterator().next().getDisplayName()), true);
         } else {
            source.sendFeedback(new TranslationTextComponent("commands.recipe.give.success.multiple", recipes.size(), targets.size()), true);
         }

         return i;
      }
   }

   private static int takeRecipes(CommandSource source, Collection<ServerPlayerEntity> targets, Collection<IRecipe<?>> recipes) throws CommandSyntaxException {
      int i = 0;

      for(ServerPlayerEntity serverplayerentity : targets) {
         i += serverplayerentity.resetRecipes(recipes);
      }

      if (i == 0) {
         throw TAKE_FAILED_EXCEPTION.create();
      } else {
         if (targets.size() == 1) {
            source.sendFeedback(new TranslationTextComponent("commands.recipe.take.success.single", recipes.size(), targets.iterator().next().getDisplayName()), true);
         } else {
            source.sendFeedback(new TranslationTextComponent("commands.recipe.take.success.multiple", recipes.size(), targets.size()), true);
         }

         return i;
      }
   }
}