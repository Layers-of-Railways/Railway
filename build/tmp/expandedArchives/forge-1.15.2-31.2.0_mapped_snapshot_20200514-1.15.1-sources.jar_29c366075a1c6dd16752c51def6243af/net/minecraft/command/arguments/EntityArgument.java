package net.minecraft.command.arguments;

import com.google.common.collect.Iterables;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TranslationTextComponent;

public class EntityArgument implements ArgumentType<EntitySelector> {
   private static final Collection<String> EXAMPLES = Arrays.asList("Player", "0123", "@e", "@e[type=foo]", "dd12be42-52a9-4a91-a8a1-11c01849e498");
   public static final SimpleCommandExceptionType TOO_MANY_ENTITIES = new SimpleCommandExceptionType(new TranslationTextComponent("argument.entity.toomany"));
   public static final SimpleCommandExceptionType TOO_MANY_PLAYERS = new SimpleCommandExceptionType(new TranslationTextComponent("argument.player.toomany"));
   public static final SimpleCommandExceptionType ONLY_PLAYERS_ALLOWED = new SimpleCommandExceptionType(new TranslationTextComponent("argument.player.entities"));
   public static final SimpleCommandExceptionType ENTITY_NOT_FOUND = new SimpleCommandExceptionType(new TranslationTextComponent("argument.entity.notfound.entity"));
   public static final SimpleCommandExceptionType PLAYER_NOT_FOUND = new SimpleCommandExceptionType(new TranslationTextComponent("argument.entity.notfound.player"));
   public static final SimpleCommandExceptionType SELECTOR_NOT_ALLOWED = new SimpleCommandExceptionType(new TranslationTextComponent("argument.entity.selector.not_allowed"));
   private final boolean single;
   private final boolean playersOnly;

   protected EntityArgument(boolean singleIn, boolean playersOnlyIn) {
      this.single = singleIn;
      this.playersOnly = playersOnlyIn;
   }

   public static EntityArgument entity() {
      return new EntityArgument(true, false);
   }

   public static Entity getEntity(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
      return context.getArgument(name, EntitySelector.class).selectOne(context.getSource());
   }

   public static EntityArgument entities() {
      return new EntityArgument(false, false);
   }

   public static Collection<? extends Entity> getEntities(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
      Collection<? extends Entity> collection = getEntitiesAllowingNone(context, name);
      if (collection.isEmpty()) {
         throw ENTITY_NOT_FOUND.create();
      } else {
         return collection;
      }
   }

   public static Collection<? extends Entity> getEntitiesAllowingNone(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
      return context.getArgument(name, EntitySelector.class).select(context.getSource());
   }

   public static Collection<ServerPlayerEntity> getPlayersAllowingNone(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
      return context.getArgument(name, EntitySelector.class).selectPlayers(context.getSource());
   }

   public static EntityArgument player() {
      return new EntityArgument(true, true);
   }

   public static ServerPlayerEntity getPlayer(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
      return context.getArgument(name, EntitySelector.class).selectOnePlayer(context.getSource());
   }

   public static EntityArgument players() {
      return new EntityArgument(false, true);
   }

   public static Collection<ServerPlayerEntity> getPlayers(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
      List<ServerPlayerEntity> list = context.getArgument(name, EntitySelector.class).selectPlayers(context.getSource());
      if (list.isEmpty()) {
         throw PLAYER_NOT_FOUND.create();
      } else {
         return list;
      }
   }

   public EntitySelector parse(StringReader p_parse_1_) throws CommandSyntaxException {
      int i = 0;
      EntitySelectorParser entityselectorparser = new EntitySelectorParser(p_parse_1_);
      EntitySelector entityselector = entityselectorparser.parse();
      if (entityselector.getLimit() > 1 && this.single) {
         if (this.playersOnly) {
            p_parse_1_.setCursor(0);
            throw TOO_MANY_PLAYERS.createWithContext(p_parse_1_);
         } else {
            p_parse_1_.setCursor(0);
            throw TOO_MANY_ENTITIES.createWithContext(p_parse_1_);
         }
      } else if (entityselector.includesEntities() && this.playersOnly && !entityselector.isSelfSelector()) {
         p_parse_1_.setCursor(0);
         throw ONLY_PLAYERS_ALLOWED.createWithContext(p_parse_1_);
      } else {
         return entityselector;
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      if (p_listSuggestions_1_.getSource() instanceof ISuggestionProvider) {
         StringReader stringreader = new StringReader(p_listSuggestions_2_.getInput());
         stringreader.setCursor(p_listSuggestions_2_.getStart());
         ISuggestionProvider isuggestionprovider = (ISuggestionProvider)p_listSuggestions_1_.getSource();
         EntitySelectorParser entityselectorparser = new EntitySelectorParser(stringreader, isuggestionprovider.hasPermissionLevel(2));

         try {
            entityselectorparser.parse();
         } catch (CommandSyntaxException var7) {
            ;
         }

         return entityselectorparser.fillSuggestions(p_listSuggestions_2_, (p_201942_2_) -> {
            Collection<String> collection = isuggestionprovider.getPlayerNames();
            Iterable<String> iterable = (Iterable<String>)(this.playersOnly ? collection : Iterables.concat(collection, isuggestionprovider.getTargetedEntity()));
            ISuggestionProvider.suggest(iterable, p_201942_2_);
         });
      } else {
         return Suggestions.empty();
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   public static class Serializer implements IArgumentSerializer<EntityArgument> {
      public void write(EntityArgument argument, PacketBuffer buffer) {
         byte b0 = 0;
         if (argument.single) {
            b0 = (byte)(b0 | 1);
         }

         if (argument.playersOnly) {
            b0 = (byte)(b0 | 2);
         }

         buffer.writeByte(b0);
      }

      public EntityArgument read(PacketBuffer buffer) {
         byte b0 = buffer.readByte();
         return new EntityArgument((b0 & 1) != 0, (b0 & 2) != 0);
      }

      public void write(EntityArgument p_212244_1_, JsonObject p_212244_2_) {
         p_212244_2_.addProperty("amount", p_212244_1_.single ? "single" : "multiple");
         p_212244_2_.addProperty("type", p_212244_1_.playersOnly ? "players" : "entities");
      }
   }
}