package net.minecraft.command.arguments;

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
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

public class BlockPosArgument implements ArgumentType<ILocationArgument> {
   private static final Collection<String> EXAMPLES = Arrays.asList("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "~0.5 ~1 ~-5");
   public static final SimpleCommandExceptionType POS_UNLOADED = new SimpleCommandExceptionType(new TranslationTextComponent("argument.pos.unloaded"));
   public static final SimpleCommandExceptionType POS_OUT_OF_WORLD = new SimpleCommandExceptionType(new TranslationTextComponent("argument.pos.outofworld"));

   public static BlockPosArgument blockPos() {
      return new BlockPosArgument();
   }

   public static BlockPos getLoadedBlockPos(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
      BlockPos blockpos = context.getArgument(name, ILocationArgument.class).getBlockPos(context.getSource());
      if (!context.getSource().getWorld().isBlockLoaded(blockpos)) {
         throw POS_UNLOADED.create();
      } else {
         context.getSource().getWorld();
         if (!ServerWorld.isValid(blockpos)) {
            throw POS_OUT_OF_WORLD.create();
         } else {
            return blockpos;
         }
      }
   }

   public static BlockPos getBlockPos(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
      return context.getArgument(name, ILocationArgument.class).getBlockPos(context.getSource());
   }

   public ILocationArgument parse(StringReader p_parse_1_) throws CommandSyntaxException {
      return (ILocationArgument)(p_parse_1_.canRead() && p_parse_1_.peek() == '^' ? LocalLocationArgument.parse(p_parse_1_) : LocationInput.parseInt(p_parse_1_));
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      if (!(p_listSuggestions_1_.getSource() instanceof ISuggestionProvider)) {
         return Suggestions.empty();
      } else {
         String s = p_listSuggestions_2_.getRemaining();
         Collection<ISuggestionProvider.Coordinates> collection;
         if (!s.isEmpty() && s.charAt(0) == '^') {
            collection = Collections.singleton(ISuggestionProvider.Coordinates.DEFAULT_LOCAL);
         } else {
            collection = ((ISuggestionProvider)p_listSuggestions_1_.getSource()).func_217294_q();
         }

         return ISuggestionProvider.func_209000_a(s, collection, p_listSuggestions_2_, Commands.func_212590_a(this::parse));
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}