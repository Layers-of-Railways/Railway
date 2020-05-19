package net.minecraft.client.renderer.model;

import java.util.Locale;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ModelResourceLocation extends ResourceLocation {
   private final String variant;

   protected ModelResourceLocation(String[] p_i48111_1_) {
      super(p_i48111_1_);
      this.variant = p_i48111_1_[2].toLowerCase(Locale.ROOT);
   }

   public ModelResourceLocation(String pathIn) {
      this(parsePathString(pathIn));
   }

   public ModelResourceLocation(ResourceLocation location, String variantIn) {
      this(location.toString(), variantIn);
   }

   public ModelResourceLocation(String location, String variantIn) {
      this(parsePathString(location + '#' + variantIn));
   }

   protected static String[] parsePathString(String pathIn) {
      String[] astring = new String[]{null, pathIn, ""};
      int i = pathIn.indexOf(35);
      String s = pathIn;
      if (i >= 0) {
         astring[2] = pathIn.substring(i + 1, pathIn.length());
         if (i > 1) {
            s = pathIn.substring(0, i);
         }
      }

      System.arraycopy(ResourceLocation.decompose(s, ':'), 0, astring, 0, 2);
      return astring;
   }

   public String getVariant() {
      return this.variant;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ instanceof ModelResourceLocation && super.equals(p_equals_1_)) {
         ModelResourceLocation modelresourcelocation = (ModelResourceLocation)p_equals_1_;
         return this.variant.equals(modelresourcelocation.variant);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return 31 * super.hashCode() + this.variant.hashCode();
   }

   public String toString() {
      return super.toString() + '#' + this.variant;
   }
}