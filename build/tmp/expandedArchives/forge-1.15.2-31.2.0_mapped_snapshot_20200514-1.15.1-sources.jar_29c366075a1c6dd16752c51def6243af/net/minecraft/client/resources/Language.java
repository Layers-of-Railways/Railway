package net.minecraft.client.resources;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Language implements com.mojang.bridge.game.Language, Comparable<Language> {
   private final String languageCode;
   private final String region;
   private final String name;
   private final boolean bidirectional;

   public Language(String languageCodeIn, String regionIn, String nameIn, boolean bidirectionalIn) {
      this.languageCode = languageCodeIn;
      this.region = regionIn;
      this.name = nameIn;
      this.bidirectional = bidirectionalIn;
      String[] splitLangCode = name.split("_", 2);
      if (splitLangCode.length == 1) { // Vanilla has some languages without underscores
          this.javaLocale = new java.util.Locale(languageCode);
      } else {
          this.javaLocale = new java.util.Locale(splitLangCode[0], splitLangCode[1]);
      }
   }

   public String getCode() {
      return this.languageCode;
   }

   public String getName() {
      return this.name;
   }

   public String getRegion() {
      return this.region;
   }

   public boolean isBidirectional() {
      return this.bidirectional;
   }

   public String toString() {
      return String.format("%s (%s)", this.name, this.region);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return !(p_equals_1_ instanceof Language) ? false : this.languageCode.equals(((Language)p_equals_1_).languageCode);
      }
   }

   public int hashCode() {
      return this.languageCode.hashCode();
   }

   public int compareTo(Language p_compareTo_1_) {
      return this.languageCode.compareTo(p_compareTo_1_.languageCode);
   }

   // Forge: add access to Locale so modders can create correct string and number formatters
   private final java.util.Locale javaLocale;
   public java.util.Locale getJavaLocale() { return javaLocale; }
}