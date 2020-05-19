package net.minecraft.command.arguments;

import com.google.common.collect.Streams;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.dimension.DimensionType;

public class DimensionArgument implements ArgumentType<DimensionType> {
   private static final Collection<String> EXAMPLES = Stream.of(DimensionType.OVERWORLD, DimensionType.THE_NETHER).map((p_212593_0_) -> {
      return DimensionType.getKey(p_212593_0_).toString();
   }).collect(Collectors.toList());
   public static final DynamicCommandExceptionType field_212596_a = new DynamicCommandExceptionType((p_212594_0_) -> {
      return new TranslationTextComponent("argument.dimension.invalid", p_212594_0_);
   });

   public DimensionType parse(StringReader p_parse_1_) throws CommandSyntaxException {
      ResourceLocation resourcelocation = ResourceLocation.read(p_parse_1_);
      return Registry.DIMENSION_TYPE.getValue(resourcelocation).orElseThrow(() -> {
         return field_212596_a.create(resourcelocation);
      });
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      return ISuggestionProvider.func_212476_a(Streams.stream(DimensionType.getAll()).map(DimensionType::getKey), p_listSuggestions_2_);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   public static DimensionArgument getDimension() {
      return new DimensionArgument();
   }

   public static DimensionType getDimensionArgument(CommandContext<CommandSource> p_212592_0_, String p_212592_1_) {
      return p_212592_0_.getArgument(p_212592_1_, DimensionType.class);
   }
}