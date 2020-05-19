package net.minecraft.command.arguments;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TranslationTextComponent;

public class ScoreHolderArgument implements ArgumentType<ScoreHolderArgument.INameProvider> {
   public static final SuggestionProvider<CommandSource> SUGGEST_ENTITY_SELECTOR = (p_201323_0_, p_201323_1_) -> {
      StringReader stringreader = new StringReader(p_201323_1_.getInput());
      stringreader.setCursor(p_201323_1_.getStart());
      EntitySelectorParser entityselectorparser = new EntitySelectorParser(stringreader);

      try {
         entityselectorparser.parse();
      } catch (CommandSyntaxException var5) {
         ;
      }

      return entityselectorparser.fillSuggestions(p_201323_1_, (p_201949_1_) -> {
         ISuggestionProvider.suggest(p_201323_0_.getSource().getPlayerNames(), p_201949_1_);
      });
   };
   private static final Collection<String> EXAMPLES = Arrays.asList("Player", "0123", "*", "@e");
   private static final SimpleCommandExceptionType EMPTY_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("argument.scoreHolder.empty"));
   private final boolean allowMultiple;

   public ScoreHolderArgument(boolean allowMultipleIn) {
      this.allowMultiple = allowMultipleIn;
   }

   /**
    * Gets a single score holder, with no objectives list.
    */
   public static String getSingleScoreHolderNoObjectives(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
      return getScoreHolderNoObjectives(context, name).iterator().next();
   }

   /**
    * Gets one or more score holders, with no objectives list.
    */
   public static Collection<String> getScoreHolderNoObjectives(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
      return getScoreHolder(context, name, Collections::emptyList);
   }

   /**
    * Gets one or more score holders, using the server's complete list of objectives.
    */
   public static Collection<String> getScoreHolder(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
      return getScoreHolder(context, name, context.getSource().getServer().getScoreboard()::getObjectiveNames);
   }

   /**
    * Gets one or more score holders.
    */
   public static Collection<String> getScoreHolder(CommandContext<CommandSource> context, String name, Supplier<Collection<String>> objectives) throws CommandSyntaxException {
      Collection<String> collection = context.getArgument(name, ScoreHolderArgument.INameProvider.class).getNames(context.getSource(), objectives);
      if (collection.isEmpty()) {
         throw EntityArgument.ENTITY_NOT_FOUND.create();
      } else {
         return collection;
      }
   }

   public static ScoreHolderArgument scoreHolder() {
      return new ScoreHolderArgument(false);
   }

   public static ScoreHolderArgument scoreHolders() {
      return new ScoreHolderArgument(true);
   }

   public ScoreHolderArgument.INameProvider parse(StringReader p_parse_1_) throws CommandSyntaxException {
      if (p_parse_1_.canRead() && p_parse_1_.peek() == '@') {
         EntitySelectorParser entityselectorparser = new EntitySelectorParser(p_parse_1_);
         EntitySelector entityselector = entityselectorparser.parse();
         if (!this.allowMultiple && entityselector.getLimit() > 1) {
            throw EntityArgument.TOO_MANY_ENTITIES.create();
         } else {
            return new ScoreHolderArgument.NameProvider(entityselector);
         }
      } else {
         int i = p_parse_1_.getCursor();

         while(p_parse_1_.canRead() && p_parse_1_.peek() != ' ') {
            p_parse_1_.skip();
         }

         String s = p_parse_1_.getString().substring(i, p_parse_1_.getCursor());
         if (s.equals("*")) {
            return (p_197208_0_, p_197208_1_) -> {
               Collection<String> collection1 = (Collection)p_197208_1_.get();
               if (collection1.isEmpty()) {
                  throw EMPTY_EXCEPTION.create();
               } else {
                  return collection1;
               }
            };
         } else {
            Collection<String> collection = Collections.singleton(s);
            return (p_197212_1_, p_197212_2_) -> {
               return collection;
            };
         }
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   @FunctionalInterface
   public interface INameProvider {
      Collection<String> getNames(CommandSource p_getNames_1_, Supplier<Collection<String>> p_getNames_2_) throws CommandSyntaxException;
   }

   public static class NameProvider implements ScoreHolderArgument.INameProvider {
      private final EntitySelector selector;

      public NameProvider(EntitySelector selectorIn) {
         this.selector = selectorIn;
      }

      public Collection<String> getNames(CommandSource p_getNames_1_, Supplier<Collection<String>> p_getNames_2_) throws CommandSyntaxException {
         List<? extends Entity> list = this.selector.select(p_getNames_1_);
         if (list.isEmpty()) {
            throw EntityArgument.ENTITY_NOT_FOUND.create();
         } else {
            List<String> list1 = Lists.newArrayList();

            for(Entity entity : list) {
               list1.add(entity.getScoreboardName());
            }

            return list1;
         }
      }
   }

   public static class Serializer implements IArgumentSerializer<ScoreHolderArgument> {
      public void write(ScoreHolderArgument argument, PacketBuffer buffer) {
         byte b0 = 0;
         if (argument.allowMultiple) {
            b0 = (byte)(b0 | 1);
         }

         buffer.writeByte(b0);
      }

      public ScoreHolderArgument read(PacketBuffer buffer) {
         byte b0 = buffer.readByte();
         boolean flag = (b0 & 1) != 0;
         return new ScoreHolderArgument(flag);
      }

      public void write(ScoreHolderArgument p_212244_1_, JsonObject p_212244_2_) {
         p_212244_2_.addProperty("amount", p_212244_1_.allowMultiple ? "multiple" : "single");
      }
   }
}