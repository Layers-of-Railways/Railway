package com.mojang.realmsclient.gui;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum ChatFormatting {
   BLACK('0'),
   DARK_BLUE('1'),
   DARK_GREEN('2'),
   DARK_AQUA('3'),
   DARK_RED('4'),
   DARK_PURPLE('5'),
   GOLD('6'),
   GRAY('7'),
   DARK_GRAY('8'),
   BLUE('9'),
   GREEN('a'),
   AQUA('b'),
   RED('c'),
   LIGHT_PURPLE('d'),
   YELLOW('e'),
   WHITE('f'),
   OBFUSCATED('k', true),
   BOLD('l', true),
   STRIKETHROUGH('m', true),
   UNDERLINE('n', true),
   ITALIC('o', true),
   RESET('r');

   private static final Map<Character, ChatFormatting> field_225044_w = Arrays.stream(values()).collect(Collectors.toMap(ChatFormatting::func_225041_a, (p_225039_0_) -> {
      return p_225039_0_;
   }));
   private static final Map<String, ChatFormatting> field_225045_x = Arrays.stream(values()).collect(Collectors.toMap(ChatFormatting::func_225038_b, (p_225040_0_) -> {
      return p_225040_0_;
   }));
   private static final Pattern field_225046_y = Pattern.compile("(?i)\u00a7[0-9A-FK-OR]");
   private final char field_225047_z;
   private final boolean field_225042_A;
   private final String field_225043_B;

   private ChatFormatting(char p_i51781_3_) {
      this(p_i51781_3_, false);
   }

   private ChatFormatting(char p_i51782_3_, boolean p_i51782_4_) {
      this.field_225047_z = p_i51782_3_;
      this.field_225042_A = p_i51782_4_;
      this.field_225043_B = "\u00a7" + p_i51782_3_;
   }

   public char func_225041_a() {
      return this.field_225047_z;
   }

   public String func_225038_b() {
      return this.name().toLowerCase(Locale.ROOT);
   }

   public String toString() {
      return this.field_225043_B;
   }
}