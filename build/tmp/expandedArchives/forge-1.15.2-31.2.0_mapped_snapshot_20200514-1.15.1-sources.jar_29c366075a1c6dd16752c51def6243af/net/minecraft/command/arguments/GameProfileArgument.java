package net.minecraft.command.arguments;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

public class GameProfileArgument implements ArgumentType<GameProfileArgument.IProfileProvider> {
   private static final Collection<String> EXAMPLES = Arrays.asList("Player", "0123", "dd12be42-52a9-4a91-a8a1-11c01849e498", "@e");
   public static final SimpleCommandExceptionType PLAYER_UNKNOWN = new SimpleCommandExceptionType(new TranslationTextComponent("argument.player.unknown"));

   public static Collection<GameProfile> getGameProfiles(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
      return context.getArgument(name, GameProfileArgument.IProfileProvider.class).getNames(context.getSource());
   }

   public static GameProfileArgument gameProfile() {
      return new GameProfileArgument();
   }

   public GameProfileArgument.IProfileProvider parse(StringReader p_parse_1_) throws CommandSyntaxException {
      if (p_parse_1_.canRead() && p_parse_1_.peek() == '@') {
         EntitySelectorParser entityselectorparser = new EntitySelectorParser(p_parse_1_);
         EntitySelector entityselector = entityselectorparser.parse();
         if (entityselector.includesEntities()) {
            throw EntityArgument.ONLY_PLAYERS_ALLOWED.create();
         } else {
            return new GameProfileArgument.ProfileProvider(entityselector);
         }
      } else {
         int i = p_parse_1_.getCursor();

         while(p_parse_1_.canRead() && p_parse_1_.peek() != ' ') {
            p_parse_1_.skip();
         }

         String s = p_parse_1_.getString().substring(i, p_parse_1_.getCursor());
         return (p_197107_1_) -> {
            GameProfile gameprofile = p_197107_1_.getServer().getPlayerProfileCache().getGameProfileForUsername(s);
            if (gameprofile == null) {
               throw PLAYER_UNKNOWN.create();
            } else {
               return Collections.singleton(gameprofile);
            }
         };
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      if (p_listSuggestions_1_.getSource() instanceof ISuggestionProvider) {
         StringReader stringreader = new StringReader(p_listSuggestions_2_.getInput());
         stringreader.setCursor(p_listSuggestions_2_.getStart());
         EntitySelectorParser entityselectorparser = new EntitySelectorParser(stringreader);

         try {
            entityselectorparser.parse();
         } catch (CommandSyntaxException var6) {
            ;
         }

         return entityselectorparser.fillSuggestions(p_listSuggestions_2_, (p_201943_1_) -> {
            ISuggestionProvider.suggest(((ISuggestionProvider)p_listSuggestions_1_.getSource()).getPlayerNames(), p_201943_1_);
         });
      } else {
         return Suggestions.empty();
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   @FunctionalInterface
   public interface IProfileProvider {
      Collection<GameProfile> getNames(CommandSource p_getNames_1_) throws CommandSyntaxException;
   }

   public static class ProfileProvider implements GameProfileArgument.IProfileProvider {
      private final EntitySelector selector;

      public ProfileProvider(EntitySelector selectorIn) {
         this.selector = selectorIn;
      }

      public Collection<GameProfile> getNames(CommandSource p_getNames_1_) throws CommandSyntaxException {
         List<ServerPlayerEntity> list = this.selector.selectPlayers(p_getNames_1_);
         if (list.isEmpty()) {
            throw EntityArgument.PLAYER_NOT_FOUND.create();
         } else {
            List<GameProfile> list1 = Lists.newArrayList();

            for(ServerPlayerEntity serverplayerentity : list) {
               list1.add(serverplayerentity.getGameProfile());
            }

            return list1;
         }
      }
   }
}