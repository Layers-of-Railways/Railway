package net.minecraft.command.impl;

import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Set;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TranslationTextComponent;

public class TagCommand {
   private static final SimpleCommandExceptionType ADD_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.tag.add.failed"));
   private static final SimpleCommandExceptionType REMOVE_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.tag.remove.failed"));

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("tag").requires((p_198751_0_) -> {
         return p_198751_0_.hasPermissionLevel(2);
      }).then(Commands.argument("targets", EntityArgument.entities()).then(Commands.literal("add").then(Commands.argument("name", StringArgumentType.word()).executes((p_198746_0_) -> {
         return addTag(p_198746_0_.getSource(), EntityArgument.getEntities(p_198746_0_, "targets"), StringArgumentType.getString(p_198746_0_, "name"));
      }))).then(Commands.literal("remove").then(Commands.argument("name", StringArgumentType.word()).suggests((p_198745_0_, p_198745_1_) -> {
         return ISuggestionProvider.suggest(getAllTags(EntityArgument.getEntities(p_198745_0_, "targets")), p_198745_1_);
      }).executes((p_198742_0_) -> {
         return removeTag(p_198742_0_.getSource(), EntityArgument.getEntities(p_198742_0_, "targets"), StringArgumentType.getString(p_198742_0_, "name"));
      }))).then(Commands.literal("list").executes((p_198747_0_) -> {
         return listTags(p_198747_0_.getSource(), EntityArgument.getEntities(p_198747_0_, "targets"));
      }))));
   }

   /**
    * Gets all tags that are present on at least one of the given entities.
    */
   private static Collection<String> getAllTags(Collection<? extends Entity> entities) {
      Set<String> set = Sets.newHashSet();

      for(Entity entity : entities) {
         set.addAll(entity.getTags());
      }

      return set;
   }

   private static int addTag(CommandSource source, Collection<? extends Entity> entities, String tagName) throws CommandSyntaxException {
      int i = 0;

      for(Entity entity : entities) {
         if (entity.addTag(tagName)) {
            ++i;
         }
      }

      if (i == 0) {
         throw ADD_FAILED.create();
      } else {
         if (entities.size() == 1) {
            source.sendFeedback(new TranslationTextComponent("commands.tag.add.success.single", tagName, entities.iterator().next().getDisplayName()), true);
         } else {
            source.sendFeedback(new TranslationTextComponent("commands.tag.add.success.multiple", tagName, entities.size()), true);
         }

         return i;
      }
   }

   private static int removeTag(CommandSource source, Collection<? extends Entity> entities, String tagName) throws CommandSyntaxException {
      int i = 0;

      for(Entity entity : entities) {
         if (entity.removeTag(tagName)) {
            ++i;
         }
      }

      if (i == 0) {
         throw REMOVE_FAILED.create();
      } else {
         if (entities.size() == 1) {
            source.sendFeedback(new TranslationTextComponent("commands.tag.remove.success.single", tagName, entities.iterator().next().getDisplayName()), true);
         } else {
            source.sendFeedback(new TranslationTextComponent("commands.tag.remove.success.multiple", tagName, entities.size()), true);
         }

         return i;
      }
   }

   private static int listTags(CommandSource source, Collection<? extends Entity> entities) {
      Set<String> set = Sets.newHashSet();

      for(Entity entity : entities) {
         set.addAll(entity.getTags());
      }

      if (entities.size() == 1) {
         Entity entity1 = entities.iterator().next();
         if (set.isEmpty()) {
            source.sendFeedback(new TranslationTextComponent("commands.tag.list.single.empty", entity1.getDisplayName()), false);
         } else {
            source.sendFeedback(new TranslationTextComponent("commands.tag.list.single.success", entity1.getDisplayName(), set.size(), TextComponentUtils.makeGreenSortedList(set)), false);
         }
      } else if (set.isEmpty()) {
         source.sendFeedback(new TranslationTextComponent("commands.tag.list.multiple.empty", entities.size()), false);
      } else {
         source.sendFeedback(new TranslationTextComponent("commands.tag.list.multiple.success", entities.size(), set.size(), TextComponentUtils.makeGreenSortedList(set)), false);
      }

      return set.size();
   }
}