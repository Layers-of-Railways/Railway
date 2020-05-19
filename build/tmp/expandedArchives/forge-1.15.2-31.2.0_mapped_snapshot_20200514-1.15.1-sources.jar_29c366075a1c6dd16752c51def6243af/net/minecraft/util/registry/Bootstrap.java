package net.minecraft.util.registry;

import java.io.PrintStream;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.command.arguments.EntityOptions;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.potion.Effect;
import net.minecraft.potion.PotionBrewing;
import net.minecraft.server.DebugLoggingPrintStream;
import net.minecraft.util.LoggingPrintStream;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.world.biome.Biome;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Bootstrap {
   public static final PrintStream SYSOUT = System.out;
   /** Whether the blocks, items, etc have already been registered */
   private static boolean alreadyRegistered;
   private static final Logger LOGGER = LogManager.getLogger();

   /**
    * Registers blocks, items, stats, etc.
    */
   public static void register() {
      if (!alreadyRegistered) {
         alreadyRegistered = true;
         if (Registry.REGISTRY.isEmpty()) {
            throw new IllegalStateException("Unable to load registries");
         } else {
            FireBlock.init();
            ComposterBlock.init();
            if (EntityType.getKey(EntityType.PLAYER) == null) {
               throw new IllegalStateException("Failed loading EntityTypes");
            } else {
               PotionBrewing.init();
               EntityOptions.registerOptions();
               IDispenseItemBehavior.init();
               ArgumentTypes.registerArgumentTypes();
               if (false) // skip redirectOutputToLog, Forge already redirects stdout and stderr output to log so that they print with more context
               redirectOutputToLog();
            }
         }
      }
   }

   private static <T> void func_218819_a(Registry<T> p_218819_0_, Function<T, String> p_218819_1_, Set<String> p_218819_2_) {
      LanguageMap languagemap = LanguageMap.getInstance();
      p_218819_0_.iterator().forEachRemaining((p_218818_3_) -> {
         String s = p_218819_1_.apply(p_218818_3_);
         if (!languagemap.exists(s)) {
            p_218819_2_.add(s);
         }

      });
   }

   public static Set<String> func_218816_b() {
      Set<String> set = new TreeSet<>();
      func_218819_a(Registry.ENTITY_TYPE, EntityType::getTranslationKey, set);
      func_218819_a(Registry.EFFECTS, Effect::getName, set);
      func_218819_a(Registry.ITEM, Item::getTranslationKey, set);
      func_218819_a(Registry.ENCHANTMENT, Enchantment::getName, set);
      func_218819_a(Registry.BIOME, Biome::getTranslationKey, set);
      func_218819_a(Registry.BLOCK, Block::getTranslationKey, set);
      func_218819_a(Registry.CUSTOM_STAT, (p_218820_0_) -> {
         return "stat." + p_218820_0_.toString().replace(':', '.');
      }, set);
      return set;
   }

   public static void checkTranslations() {
      if (!alreadyRegistered) {
         throw new IllegalArgumentException("Not bootstrapped");
      } else {
         if (SharedConstants.developmentMode) {
            func_218816_b().forEach((p_218817_0_) -> {
               LOGGER.error("Missing translations: " + p_218817_0_);
            });
         }

      }
   }

   /**
    * redirect standard streams to logger
    */
   private static void redirectOutputToLog() {
      if (LOGGER.isDebugEnabled()) {
         System.setErr(new DebugLoggingPrintStream("STDERR", System.err));
         System.setOut(new DebugLoggingPrintStream("STDOUT", SYSOUT));
      } else {
         System.setErr(new LoggingPrintStream("STDERR", System.err));
         System.setOut(new LoggingPrintStream("STDOUT", SYSOUT));
      }

   }

   public static void printToSYSOUT(String message) {
      SYSOUT.println(message);
   }
}