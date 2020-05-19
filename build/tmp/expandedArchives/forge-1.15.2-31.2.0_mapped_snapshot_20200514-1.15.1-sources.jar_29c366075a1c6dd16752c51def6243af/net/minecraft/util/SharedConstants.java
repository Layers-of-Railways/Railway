package net.minecraft.util;

import com.mojang.bridge.game.GameVersion;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.types.constant.NamespacedStringType;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetector.Level;
import net.minecraft.command.TranslatableExceptionProvider;
import net.minecraft.util.datafix.NamespacedSchema;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SharedConstants {
   public static final Level NETTY_LEAK_DETECTION = Level.DISABLED;
   public static boolean developmentMode;
   public static final char[] ILLEGAL_FILE_CHARACTERS = new char[]{'/', '\n', '\r', '\t', '\u0000', '\f', '`', '?', '*', '\\', '<', '>', '|', '"', ':'};
   private static GameVersion version;

   /**
    * Checks if the given character is allowed to be put into chat.
    */
   public static boolean isAllowedCharacter(char character) {
      return character != 167 && character >= ' ' && character != 127;
   }

   /**
    * Filter a string, keeping only characters for which {@link #isAllowedCharacter(char)} returns true.
    *  
    * Note that this method strips line breaks, as {@link #isAllowedCharacter(char)} returns false for those.
    * @return A filtered version of the input string
    */
   public static String filterAllowedCharacters(String input) {
      StringBuilder stringbuilder = new StringBuilder();

      for(char c0 : input.toCharArray()) {
         if (isAllowedCharacter(c0)) {
            stringbuilder.append(c0);
         }
      }

      return stringbuilder.toString();
   }

   @OnlyIn(Dist.CLIENT)
   public static String func_215070_b(String p_215070_0_) {
      StringBuilder stringbuilder = new StringBuilder();

      for(int i = 0; i < p_215070_0_.length(); i = p_215070_0_.offsetByCodePoints(i, 1)) {
         int j = p_215070_0_.codePointAt(i);
         if (!Character.isSupplementaryCodePoint(j)) {
            stringbuilder.appendCodePoint(j);
         } else {
            stringbuilder.append('\ufffd');
         }
      }

      return stringbuilder.toString();
   }

   public static GameVersion getVersion() {
      if (version == null) {
         version = MinecraftVersion.load();
      }

      return version;
   }

   static {
      if (System.getProperty("io.netty.leakDetection.level") == null) // Forge: allow level to be manually specified
      ResourceLeakDetector.setLevel(NETTY_LEAK_DETECTION);
      CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES = false;
      CommandSyntaxException.BUILT_IN_EXCEPTIONS = new TranslatableExceptionProvider();
      NamespacedStringType.ENSURE_NAMESPACE = NamespacedSchema::ensureNamespaced;
   }
}