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
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TranslationTextComponent;

public class Vec2Argument implements ArgumentType<ILocationArgument> {
   private static final Collection<String> EXAMPLES = Arrays.asList("0 0", "~ ~", "0.1 -0.5", "~1 ~-2");
   public static final SimpleCommandExceptionType VEC2_INCOMPLETE = new SimpleCommandExceptionType(new TranslationTextComponent("argument.pos2d.incomplete"));
   private final boolean centerIntegers;

   public Vec2Argument(boolean centerIntegersIn) {
      this.centerIntegers = centerIntegersIn;
   }

   public static Vec2Argument vec2() {
      return new Vec2Argument(true);
   }

   public static Vec2f getVec2f(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
      Vec3d vec3d = context.getArgument(name, ILocationArgument.class).getPosition(context.getSource());
      return new Vec2f((float)vec3d.x, (float)vec3d.z);
   }

   public ILocationArgument parse(StringReader p_parse_1_) throws CommandSyntaxException {
      int i = p_parse_1_.getCursor();
      if (!p_parse_1_.canRead()) {
         throw VEC2_INCOMPLETE.createWithContext(p_parse_1_);
      } else {
         LocationPart locationpart = LocationPart.parseDouble(p_parse_1_, this.centerIntegers);
         if (p_parse_1_.canRead() && p_parse_1_.peek() == ' ') {
            p_parse_1_.skip();
            LocationPart locationpart1 = LocationPart.parseDouble(p_parse_1_, this.centerIntegers);
            return new LocationInput(locationpart, new LocationPart(true, 0.0D), locationpart1);
         } else {
            p_parse_1_.setCursor(i);
            throw VEC2_INCOMPLETE.createWithContext(p_parse_1_);
         }
      }
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
            collection = ((ISuggestionProvider)p_listSuggestions_1_.getSource()).func_217293_r();
         }

         return ISuggestionProvider.func_211269_a(s, collection, p_listSuggestions_2_, Commands.func_212590_a(this::parse));
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}