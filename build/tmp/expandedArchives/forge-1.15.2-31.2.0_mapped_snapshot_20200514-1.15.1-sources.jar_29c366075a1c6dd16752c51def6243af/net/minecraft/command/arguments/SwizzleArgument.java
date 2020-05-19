package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import net.minecraft.command.CommandSource;
import net.minecraft.util.Direction;
import net.minecraft.util.text.TranslationTextComponent;

public class SwizzleArgument implements ArgumentType<EnumSet<Direction.Axis>> {
   private static final Collection<String> EXAMPLES = Arrays.asList("xyz", "x");
   private static final SimpleCommandExceptionType SWIZZLE_INVALID = new SimpleCommandExceptionType(new TranslationTextComponent("arguments.swizzle.invalid"));

   public static SwizzleArgument swizzle() {
      return new SwizzleArgument();
   }

   public static EnumSet<Direction.Axis> getSwizzle(CommandContext<CommandSource> context, String name) {
      return context.getArgument(name, EnumSet.class);
   }

   public EnumSet<Direction.Axis> parse(StringReader p_parse_1_) throws CommandSyntaxException {
      EnumSet<Direction.Axis> enumset = EnumSet.noneOf(Direction.Axis.class);

      while(p_parse_1_.canRead() && p_parse_1_.peek() != ' ') {
         char c0 = p_parse_1_.read();
         Direction.Axis direction$axis;
         switch(c0) {
         case 'x':
            direction$axis = Direction.Axis.X;
            break;
         case 'y':
            direction$axis = Direction.Axis.Y;
            break;
         case 'z':
            direction$axis = Direction.Axis.Z;
            break;
         default:
            throw SWIZZLE_INVALID.create();
         }

         if (enumset.contains(direction$axis)) {
            throw SWIZZLE_INVALID.create();
         }

         enumset.add(direction$axis);
      }

      return enumset;
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}