package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.datafixers.util.Either;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.command.CommandSource;
import net.minecraft.command.FunctionObject;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

public class FunctionArgument implements ArgumentType<FunctionArgument.IResult> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "#foo");
   private static final DynamicCommandExceptionType FUNCTION_UNKNOWN_TAG = new DynamicCommandExceptionType((p_208691_0_) -> {
      return new TranslationTextComponent("arguments.function.tag.unknown", p_208691_0_);
   });
   private static final DynamicCommandExceptionType FUNCTION_UNKNOWN = new DynamicCommandExceptionType((p_208694_0_) -> {
      return new TranslationTextComponent("arguments.function.unknown", p_208694_0_);
   });

   public static FunctionArgument function() {
      return new FunctionArgument();
   }

   public FunctionArgument.IResult parse(StringReader p_parse_1_) throws CommandSyntaxException {
      if (p_parse_1_.canRead() && p_parse_1_.peek() == '#') {
         p_parse_1_.skip();
         final ResourceLocation resourcelocation1 = ResourceLocation.read(p_parse_1_);
         return new FunctionArgument.IResult() {
            public Collection<FunctionObject> create(CommandContext<CommandSource> p_223252_1_) throws CommandSyntaxException {
               Tag<FunctionObject> tag = FunctionArgument.func_218111_d(p_223252_1_, resourcelocation1);
               return tag.getAllElements();
            }

            public Either<FunctionObject, Tag<FunctionObject>> func_218102_b(CommandContext<CommandSource> p_218102_1_) throws CommandSyntaxException {
               return Either.right(FunctionArgument.func_218111_d(p_218102_1_, resourcelocation1));
            }
         };
      } else {
         final ResourceLocation resourcelocation = ResourceLocation.read(p_parse_1_);
         return new FunctionArgument.IResult() {
            public Collection<FunctionObject> create(CommandContext<CommandSource> p_223252_1_) throws CommandSyntaxException {
               return Collections.singleton(FunctionArgument.func_218108_c(p_223252_1_, resourcelocation));
            }

            public Either<FunctionObject, Tag<FunctionObject>> func_218102_b(CommandContext<CommandSource> p_218102_1_) throws CommandSyntaxException {
               return Either.left(FunctionArgument.func_218108_c(p_218102_1_, resourcelocation));
            }
         };
      }
   }

   private static FunctionObject func_218108_c(CommandContext<CommandSource> p_218108_0_, ResourceLocation p_218108_1_) throws CommandSyntaxException {
      return p_218108_0_.getSource().getServer().getFunctionManager().get(p_218108_1_).orElseThrow(() -> {
         return FUNCTION_UNKNOWN.create(p_218108_1_.toString());
      });
   }

   private static Tag<FunctionObject> func_218111_d(CommandContext<CommandSource> p_218111_0_, ResourceLocation p_218111_1_) throws CommandSyntaxException {
      Tag<FunctionObject> tag = p_218111_0_.getSource().getServer().getFunctionManager().getTagCollection().get(p_218111_1_);
      if (tag == null) {
         throw FUNCTION_UNKNOWN_TAG.create(p_218111_1_.toString());
      } else {
         return tag;
      }
   }

   public static Collection<FunctionObject> getFunctions(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
      return context.getArgument(name, FunctionArgument.IResult.class).create(context);
   }

   public static Either<FunctionObject, Tag<FunctionObject>> func_218110_b(CommandContext<CommandSource> p_218110_0_, String p_218110_1_) throws CommandSyntaxException {
      return p_218110_0_.getArgument(p_218110_1_, FunctionArgument.IResult.class).func_218102_b(p_218110_0_);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   public interface IResult {
      Collection<FunctionObject> create(CommandContext<CommandSource> p_223252_1_) throws CommandSyntaxException;

      Either<FunctionObject, Tag<FunctionObject>> func_218102_b(CommandContext<CommandSource> p_218102_1_) throws CommandSyntaxException;
   }
}