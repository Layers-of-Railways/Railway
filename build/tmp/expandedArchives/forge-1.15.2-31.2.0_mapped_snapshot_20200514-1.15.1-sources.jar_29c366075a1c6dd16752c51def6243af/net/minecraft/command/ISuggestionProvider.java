package net.minecraft.command;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.util.ResourceLocation;

public interface ISuggestionProvider {
   Collection<String> getPlayerNames();

   default Collection<String> getTargetedEntity() {
      return Collections.emptyList();
   }

   Collection<String> getTeamNames();

   Collection<ResourceLocation> getSoundResourceLocations();

   Stream<ResourceLocation> getRecipeResourceLocations();

   CompletableFuture<Suggestions> getSuggestionsFromServer(CommandContext<ISuggestionProvider> context, SuggestionsBuilder suggestionsBuilder);

   default Collection<ISuggestionProvider.Coordinates> func_217294_q() {
      return Collections.singleton(ISuggestionProvider.Coordinates.DEFAULT_GLOBAL);
   }

   default Collection<ISuggestionProvider.Coordinates> func_217293_r() {
      return Collections.singleton(ISuggestionProvider.Coordinates.DEFAULT_GLOBAL);
   }

   boolean hasPermissionLevel(int level);

   static <T> void func_210512_a(Iterable<T> p_210512_0_, String p_210512_1_, Function<T, ResourceLocation> p_210512_2_, Consumer<T> p_210512_3_) {
      boolean flag = p_210512_1_.indexOf(58) > -1;

      for(T t : p_210512_0_) {
         ResourceLocation resourcelocation = p_210512_2_.apply(t);
         if (flag) {
            String s = resourcelocation.toString();
            if (s.startsWith(p_210512_1_)) {
               p_210512_3_.accept(t);
            }
         } else if (resourcelocation.getNamespace().startsWith(p_210512_1_) || resourcelocation.getNamespace().equals("minecraft") && resourcelocation.getPath().startsWith(p_210512_1_)) {
            p_210512_3_.accept(t);
         }
      }

   }

   static <T> void func_210511_a(Iterable<T> p_210511_0_, String p_210511_1_, String p_210511_2_, Function<T, ResourceLocation> p_210511_3_, Consumer<T> p_210511_4_) {
      if (p_210511_1_.isEmpty()) {
         p_210511_0_.forEach(p_210511_4_);
      } else {
         String s = Strings.commonPrefix(p_210511_1_, p_210511_2_);
         if (!s.isEmpty()) {
            String s1 = p_210511_1_.substring(s.length());
            func_210512_a(p_210511_0_, s1, p_210511_3_, p_210511_4_);
         }
      }

   }

   static CompletableFuture<Suggestions> suggestIterable(Iterable<ResourceLocation> p_197006_0_, SuggestionsBuilder p_197006_1_, String prefix) {
      String s = p_197006_1_.getRemaining().toLowerCase(Locale.ROOT);
      func_210511_a(p_197006_0_, s, prefix, (p_210519_0_) -> {
         return p_210519_0_;
      }, (p_210518_2_) -> {
         p_197006_1_.suggest(prefix + p_210518_2_);
      });
      return p_197006_1_.buildFuture();
   }

   static CompletableFuture<Suggestions> suggestIterable(Iterable<ResourceLocation> p_197014_0_, SuggestionsBuilder builder) {
      String s = builder.getRemaining().toLowerCase(Locale.ROOT);
      func_210512_a(p_197014_0_, s, (p_210517_0_) -> {
         return p_210517_0_;
      }, (p_210513_1_) -> {
         builder.suggest(p_210513_1_.toString());
      });
      return builder.buildFuture();
   }

   static <T> CompletableFuture<Suggestions> func_210514_a(Iterable<T> p_210514_0_, SuggestionsBuilder builder, Function<T, ResourceLocation> p_210514_2_, Function<T, Message> p_210514_3_) {
      String s = builder.getRemaining().toLowerCase(Locale.ROOT);
      func_210512_a(p_210514_0_, s, p_210514_2_, (p_210515_3_) -> {
         builder.suggest(p_210514_2_.apply(p_210515_3_).toString(), p_210514_3_.apply(p_210515_3_));
      });
      return builder.buildFuture();
   }

   static CompletableFuture<Suggestions> func_212476_a(Stream<ResourceLocation> p_212476_0_, SuggestionsBuilder builder) {
      return suggestIterable(p_212476_0_::iterator, builder);
   }

   static <T> CompletableFuture<Suggestions> func_201725_a(Stream<T> p_201725_0_, SuggestionsBuilder builder, Function<T, ResourceLocation> p_201725_2_, Function<T, Message> p_201725_3_) {
      return func_210514_a(p_201725_0_::iterator, builder, p_201725_2_, p_201725_3_);
   }

   static CompletableFuture<Suggestions> func_209000_a(String p_209000_0_, Collection<ISuggestionProvider.Coordinates> p_209000_1_, SuggestionsBuilder builder, Predicate<String> p_209000_3_) {
      List<String> list = Lists.newArrayList();
      if (Strings.isNullOrEmpty(p_209000_0_)) {
         for(ISuggestionProvider.Coordinates isuggestionprovider$coordinates : p_209000_1_) {
            String s = isuggestionprovider$coordinates.x + " " + isuggestionprovider$coordinates.y + " " + isuggestionprovider$coordinates.z;
            if (p_209000_3_.test(s)) {
               list.add(isuggestionprovider$coordinates.x);
               list.add(isuggestionprovider$coordinates.x + " " + isuggestionprovider$coordinates.y);
               list.add(s);
            }
         }
      } else {
         String[] astring = p_209000_0_.split(" ");
         if (astring.length == 1) {
            for(ISuggestionProvider.Coordinates isuggestionprovider$coordinates1 : p_209000_1_) {
               String s1 = astring[0] + " " + isuggestionprovider$coordinates1.y + " " + isuggestionprovider$coordinates1.z;
               if (p_209000_3_.test(s1)) {
                  list.add(astring[0] + " " + isuggestionprovider$coordinates1.y);
                  list.add(s1);
               }
            }
         } else if (astring.length == 2) {
            for(ISuggestionProvider.Coordinates isuggestionprovider$coordinates2 : p_209000_1_) {
               String s2 = astring[0] + " " + astring[1] + " " + isuggestionprovider$coordinates2.z;
               if (p_209000_3_.test(s2)) {
                  list.add(s2);
               }
            }
         }
      }

      return suggest(list, builder);
   }

   static CompletableFuture<Suggestions> func_211269_a(String p_211269_0_, Collection<ISuggestionProvider.Coordinates> p_211269_1_, SuggestionsBuilder builder, Predicate<String> p_211269_3_) {
      List<String> list = Lists.newArrayList();
      if (Strings.isNullOrEmpty(p_211269_0_)) {
         for(ISuggestionProvider.Coordinates isuggestionprovider$coordinates : p_211269_1_) {
            String s = isuggestionprovider$coordinates.x + " " + isuggestionprovider$coordinates.z;
            if (p_211269_3_.test(s)) {
               list.add(isuggestionprovider$coordinates.x);
               list.add(s);
            }
         }
      } else {
         String[] astring = p_211269_0_.split(" ");
         if (astring.length == 1) {
            for(ISuggestionProvider.Coordinates isuggestionprovider$coordinates1 : p_211269_1_) {
               String s1 = astring[0] + " " + isuggestionprovider$coordinates1.z;
               if (p_211269_3_.test(s1)) {
                  list.add(s1);
               }
            }
         }
      }

      return suggest(list, builder);
   }

   static CompletableFuture<Suggestions> suggest(Iterable<String> p_197005_0_, SuggestionsBuilder builder) {
      String s = builder.getRemaining().toLowerCase(Locale.ROOT);

      for(String s1 : p_197005_0_) {
         if (s1.toLowerCase(Locale.ROOT).startsWith(s)) {
            builder.suggest(s1);
         }
      }

      return builder.buildFuture();
   }

   static CompletableFuture<Suggestions> suggest(Stream<String> p_197013_0_, SuggestionsBuilder builder) {
      String s = builder.getRemaining().toLowerCase(Locale.ROOT);
      p_197013_0_.filter((p_197007_1_) -> {
         return p_197007_1_.toLowerCase(Locale.ROOT).startsWith(s);
      }).forEach(builder::suggest);
      return builder.buildFuture();
   }

   static CompletableFuture<Suggestions> suggest(String[] p_197008_0_, SuggestionsBuilder builder) {
      String s = builder.getRemaining().toLowerCase(Locale.ROOT);

      for(String s1 : p_197008_0_) {
         if (s1.toLowerCase(Locale.ROOT).startsWith(s)) {
            builder.suggest(s1);
         }
      }

      return builder.buildFuture();
   }

   public static class Coordinates {
      public static final ISuggestionProvider.Coordinates DEFAULT_LOCAL = new ISuggestionProvider.Coordinates("^", "^", "^");
      public static final ISuggestionProvider.Coordinates DEFAULT_GLOBAL = new ISuggestionProvider.Coordinates("~", "~", "~");
      public final String x;
      public final String y;
      public final String z;

      public Coordinates(String xIn, String yIn, String zIn) {
         this.x = xIn;
         this.y = yIn;
         this.z = zIn;
      }
   }
}