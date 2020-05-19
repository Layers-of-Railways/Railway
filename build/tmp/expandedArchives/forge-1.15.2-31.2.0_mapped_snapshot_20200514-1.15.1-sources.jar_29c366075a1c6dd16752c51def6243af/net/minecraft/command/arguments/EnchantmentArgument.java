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
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;

public class EnchantmentArgument implements ArgumentType<Enchantment> {
   private static final Collection<String> EXAMPLES = Arrays.asList("unbreaking", "silk_touch");
   public static final DynamicCommandExceptionType ENCHANTMENT_UNKNOWN = new DynamicCommandExceptionType((p_208662_0_) -> {
      return new TranslationTextComponent("enchantment.unknown", p_208662_0_);
   });

   public static EnchantmentArgument enchantment() {
      return new EnchantmentArgument();
   }

   public static Enchantment getEnchantment(CommandContext<CommandSource> context, String name) {
      return context.getArgument(name, Enchantment.class);
   }

   public Enchantment parse(StringReader p_parse_1_) throws CommandSyntaxException {
      ResourceLocation resourcelocation = ResourceLocation.read(p_parse_1_);
      return Registry.ENCHANTMENT.getValue(resourcelocation).orElseThrow(() -> {
         return ENCHANTMENT_UNKNOWN.create(resourcelocation);
      });
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      return ISuggestionProvider.suggestIterable(Registry.ENCHANTMENT.keySet(), p_listSuggestions_2_);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}