package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.JsonToNBT;

public class NBTTagArgument implements ArgumentType<INBT> {
   private static final Collection<String> field_218087_a = Arrays.asList("0", "0b", "0l", "0.0", "\"foo\"", "{foo=bar}", "[0]");

   private NBTTagArgument() {
   }

   public static NBTTagArgument func_218085_a() {
      return new NBTTagArgument();
   }

   public static <S> INBT func_218086_a(CommandContext<S> p_218086_0_, String p_218086_1_) {
      return p_218086_0_.getArgument(p_218086_1_, INBT.class);
   }

   public INBT parse(StringReader p_parse_1_) throws CommandSyntaxException {
      return (new JsonToNBT(p_parse_1_)).readValue();
   }

   public Collection<String> getExamples() {
      return field_218087_a;
   }
}