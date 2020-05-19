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
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;

public class PotionArgument implements ArgumentType<Effect> {
   private static final Collection<String> EXAMPLES = Arrays.asList("spooky", "effect");
   public static final DynamicCommandExceptionType EFFECT_NOT_FOUND = new DynamicCommandExceptionType((p_208663_0_) -> {
      return new TranslationTextComponent("effect.effectNotFound", p_208663_0_);
   });

   public static PotionArgument mobEffect() {
      return new PotionArgument();
   }

   public static Effect getMobEffect(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
      return context.getArgument(name, Effect.class);
   }

   public Effect parse(StringReader p_parse_1_) throws CommandSyntaxException {
      ResourceLocation resourcelocation = ResourceLocation.read(p_parse_1_);
      return Registry.EFFECTS.getValue(resourcelocation).orElseThrow(() -> {
         return EFFECT_NOT_FOUND.create(resourcelocation);
      });
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      return ISuggestionProvider.suggestIterable(Registry.EFFECTS.keySet(), p_listSuggestions_2_);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}