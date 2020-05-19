package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.TranslationTextComponent;

public class TimeArgument implements ArgumentType<Integer> {
   private static final Collection<String> field_218093_a = Arrays.asList("0d", "0s", "0t", "0");
   private static final SimpleCommandExceptionType field_218094_b = new SimpleCommandExceptionType(new TranslationTextComponent("argument.time.invalid_unit"));
   private static final DynamicCommandExceptionType field_218095_c = new DynamicCommandExceptionType((p_218092_0_) -> {
      return new TranslationTextComponent("argument.time.invalid_tick_count", p_218092_0_);
   });
   private static final Object2IntMap<String> field_218096_d = new Object2IntOpenHashMap<>();

   public static TimeArgument func_218091_a() {
      return new TimeArgument();
   }

   public Integer parse(StringReader p_parse_1_) throws CommandSyntaxException {
      float f = p_parse_1_.readFloat();
      String s = p_parse_1_.readUnquotedString();
      int i = field_218096_d.getOrDefault(s, 0);
      if (i == 0) {
         throw field_218094_b.create();
      } else {
         int j = Math.round(f * (float)i);
         if (j < 0) {
            throw field_218095_c.create(j);
         } else {
            return j;
         }
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      StringReader stringreader = new StringReader(p_listSuggestions_2_.getRemaining());

      try {
         stringreader.readFloat();
      } catch (CommandSyntaxException var5) {
         return p_listSuggestions_2_.buildFuture();
      }

      return ISuggestionProvider.suggest(field_218096_d.keySet(), p_listSuggestions_2_.createOffset(p_listSuggestions_2_.getStart() + stringreader.getCursor()));
   }

   public Collection<String> getExamples() {
      return field_218093_a;
   }

   static {
      field_218096_d.put("d", 24000);
      field_218096_d.put("s", 20);
      field_218096_d.put("t", 1);
      field_218096_d.put("", 1);
   }
}