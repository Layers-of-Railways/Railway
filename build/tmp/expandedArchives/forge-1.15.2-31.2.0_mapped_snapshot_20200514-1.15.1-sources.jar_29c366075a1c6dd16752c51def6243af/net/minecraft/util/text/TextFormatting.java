package net.minecraft.util.text;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum TextFormatting {
   BLACK("BLACK", '0', 0, 0),
   DARK_BLUE("DARK_BLUE", '1', 1, 170),
   DARK_GREEN("DARK_GREEN", '2', 2, 43520),
   DARK_AQUA("DARK_AQUA", '3', 3, 43690),
   DARK_RED("DARK_RED", '4', 4, 11141120),
   DARK_PURPLE("DARK_PURPLE", '5', 5, 11141290),
   GOLD("GOLD", '6', 6, 16755200),
   GRAY("GRAY", '7', 7, 11184810),
   DARK_GRAY("DARK_GRAY", '8', 8, 5592405),
   BLUE("BLUE", '9', 9, 5592575),
   GREEN("GREEN", 'a', 10, 5635925),
   AQUA("AQUA", 'b', 11, 5636095),
   RED("RED", 'c', 12, 16733525),
   LIGHT_PURPLE("LIGHT_PURPLE", 'd', 13, 16733695),
   YELLOW("YELLOW", 'e', 14, 16777045),
   WHITE("WHITE", 'f', 15, 16777215),
   OBFUSCATED("OBFUSCATED", 'k', true),
   BOLD("BOLD", 'l', true),
   STRIKETHROUGH("STRIKETHROUGH", 'm', true),
   UNDERLINE("UNDERLINE", 'n', true),
   ITALIC("ITALIC", 'o', true),
   RESET("RESET", 'r', -1, (Integer)null);

   private static final Map<String, TextFormatting> NAME_MAPPING = Arrays.stream(values()).collect(Collectors.toMap((p_199746_0_) -> {
      return lowercaseAlpha(p_199746_0_.name);
   }, (p_199747_0_) -> {
      return p_199747_0_;
   }));
   private static final Pattern FORMATTING_CODE_PATTERN = Pattern.compile("(?i)\u00a7[0-9A-FK-OR]");
   /** The name of this color/formatting */
   private final String name;
   private final char formattingCode;
   private final boolean fancyStyling;
   private final String controlString;
   /** The numerical index that represents this color */
   private final int colorIndex;
   @Nullable
   private final Integer color;

   private static String lowercaseAlpha(String string) {
      return string.toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "");
   }

   private TextFormatting(String formattingName, char formattingCodeIn, int index, @Nullable Integer colorCode) {
      this(formattingName, formattingCodeIn, false, index, colorCode);
   }

   private TextFormatting(String formattingName, char formattingCodeIn, boolean fancyStylingIn) {
      this(formattingName, formattingCodeIn, fancyStylingIn, -1, (Integer)null);
   }

   private TextFormatting(String formattingName, char formattingCodeIn, boolean fancyStylingIn, int index, @Nullable Integer colorCode) {
      this.name = formattingName;
      this.formattingCode = formattingCodeIn;
      this.fancyStyling = fancyStylingIn;
      this.colorIndex = index;
      this.color = colorCode;
      this.controlString = "\u00a7" + formattingCodeIn;
   }

   @OnlyIn(Dist.CLIENT)
   public static String getFormatString(String stringIn) {
      StringBuilder stringbuilder = new StringBuilder();
      int i = -1;
      int j = stringIn.length();

      while((i = stringIn.indexOf(167, i + 1)) != -1) {
         if (i < j - 1) {
            TextFormatting textformatting = fromFormattingCode(stringIn.charAt(i + 1));
            if (textformatting != null) {
               if (textformatting.isNormalStyle()) {
                  stringbuilder.setLength(0);
               }

               if (textformatting != RESET) {
                  stringbuilder.append((Object)textformatting);
               }
            }
         }
      }

      return stringbuilder.toString();
   }

   /**
    * Returns the numerical color index that represents this formatting
    */
   public int getColorIndex() {
      return this.colorIndex;
   }

   /**
    * False if this is just changing the color or resetting; true otherwise.
    */
   public boolean isFancyStyling() {
      return this.fancyStyling;
   }

   /**
    * Checks if this is a color code.
    */
   public boolean isColor() {
      return !this.fancyStyling && this != RESET;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public Integer getColor() {
      return this.color;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isNormalStyle() {
      return !this.fancyStyling;
   }

   /**
    * Gets the friendly name of this value.
    */
   public String getFriendlyName() {
      return this.name().toLowerCase(Locale.ROOT);
   }

   public String toString() {
      return this.controlString;
   }

   /**
    * Returns a copy of the given string, with formatting codes stripped away.
    */
   @Nullable
   public static String getTextWithoutFormattingCodes(@Nullable String text) {
      return text == null ? null : FORMATTING_CODE_PATTERN.matcher(text).replaceAll("");
   }

   /**
    * Gets a value by its friendly name; null if the given name does not map to a defined value.
    */
   @Nullable
   public static TextFormatting getValueByName(@Nullable String friendlyName) {
      return friendlyName == null ? null : NAME_MAPPING.get(lowercaseAlpha(friendlyName));
   }

   /**
    * Get a TextFormatting from it's color index
    */
   @Nullable
   public static TextFormatting fromColorIndex(int index) {
      if (index < 0) {
         return RESET;
      } else {
         for(TextFormatting textformatting : values()) {
            if (textformatting.getColorIndex() == index) {
               return textformatting;
            }
         }

         return null;
      }
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static TextFormatting fromFormattingCode(char formattingCodeIn) {
      char c0 = Character.toString(formattingCodeIn).toLowerCase(Locale.ROOT).charAt(0);

      for(TextFormatting textformatting : values()) {
         if (textformatting.formattingCode == c0) {
            return textformatting;
         }
      }

      return null;
   }

   /**
    * Gets all the valid values.
    */
   public static Collection<String> getValidValues(boolean getColor, boolean getFancyStyling) {
      List<String> list = Lists.newArrayList();

      for(TextFormatting textformatting : values()) {
         if ((!textformatting.isColor() || getColor) && (!textformatting.isFancyStyling() || getFancyStyling)) {
            list.add(textformatting.getFriendlyName());
         }
      }

      return list;
   }
}