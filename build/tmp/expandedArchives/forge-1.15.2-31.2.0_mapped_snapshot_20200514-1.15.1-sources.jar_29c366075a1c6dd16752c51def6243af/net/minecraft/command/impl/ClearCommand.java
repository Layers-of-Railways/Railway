package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ItemPredicateArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;

public class ClearCommand {
   private static final DynamicCommandExceptionType SINGLE_FAILED_EXCEPTION = new DynamicCommandExceptionType((p_208785_0_) -> {
      return new TranslationTextComponent("clear.failed.single", p_208785_0_);
   });
   private static final DynamicCommandExceptionType MULTIPLE_FAILED_EXCEPTION = new DynamicCommandExceptionType((p_208787_0_) -> {
      return new TranslationTextComponent("clear.failed.multiple", p_208787_0_);
   });

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("clear").requires((p_198247_0_) -> {
         return p_198247_0_.hasPermissionLevel(2);
      }).executes((p_198241_0_) -> {
         return clearInventory(p_198241_0_.getSource(), Collections.singleton(p_198241_0_.getSource().asPlayer()), (p_198248_0_) -> {
            return true;
         }, -1);
      }).then(Commands.argument("targets", EntityArgument.players()).executes((p_198245_0_) -> {
         return clearInventory(p_198245_0_.getSource(), EntityArgument.getPlayers(p_198245_0_, "targets"), (p_198242_0_) -> {
            return true;
         }, -1);
      }).then(Commands.argument("item", ItemPredicateArgument.itemPredicate()).executes((p_198240_0_) -> {
         return clearInventory(p_198240_0_.getSource(), EntityArgument.getPlayers(p_198240_0_, "targets"), ItemPredicateArgument.getItemPredicate(p_198240_0_, "item"), -1);
      }).then(Commands.argument("maxCount", IntegerArgumentType.integer(0)).executes((p_198246_0_) -> {
         return clearInventory(p_198246_0_.getSource(), EntityArgument.getPlayers(p_198246_0_, "targets"), ItemPredicateArgument.getItemPredicate(p_198246_0_, "item"), IntegerArgumentType.getInteger(p_198246_0_, "maxCount"));
      })))));
   }

   private static int clearInventory(CommandSource source, Collection<ServerPlayerEntity> targetPlayers, Predicate<ItemStack> itemPredicateIn, int maxCount) throws CommandSyntaxException {
      int i = 0;

      for(ServerPlayerEntity serverplayerentity : targetPlayers) {
         i += serverplayerentity.inventory.clearMatchingItems(itemPredicateIn, maxCount);
         serverplayerentity.openContainer.detectAndSendChanges();
         serverplayerentity.updateHeldItem();
      }

      if (i == 0) {
         if (targetPlayers.size() == 1) {
            throw SINGLE_FAILED_EXCEPTION.create(targetPlayers.iterator().next().getName().getFormattedText());
         } else {
            throw MULTIPLE_FAILED_EXCEPTION.create(targetPlayers.size());
         }
      } else {
         if (maxCount == 0) {
            if (targetPlayers.size() == 1) {
               source.sendFeedback(new TranslationTextComponent("commands.clear.test.single", i, targetPlayers.iterator().next().getDisplayName()), true);
            } else {
               source.sendFeedback(new TranslationTextComponent("commands.clear.test.multiple", i, targetPlayers.size()), true);
            }
         } else if (targetPlayers.size() == 1) {
            source.sendFeedback(new TranslationTextComponent("commands.clear.success.single", i, targetPlayers.iterator().next().getDisplayName()), true);
         } else {
            source.sendFeedback(new TranslationTextComponent("commands.clear.success.multiple", i, targetPlayers.size()), true);
         }

         return i;
      }
   }
}