package net.minecraft.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Locale;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class JSONBlendingMode {
   private static JSONBlendingMode lastApplied;
   private final int srcColorFactor;
   private final int srcAlphaFactor;
   private final int destColorFactor;
   private final int destAlphaFactor;
   private final int blendFunction;
   private final boolean separateBlend;
   private final boolean opaque;

   private JSONBlendingMode(boolean separateBlendIn, boolean opaqueIn, int srcColorFactorIn, int destColorFactorIn, int srcAlphaFactorIn, int destAlphaFactorIn, int blendFunctionIn) {
      this.separateBlend = separateBlendIn;
      this.srcColorFactor = srcColorFactorIn;
      this.destColorFactor = destColorFactorIn;
      this.srcAlphaFactor = srcAlphaFactorIn;
      this.destAlphaFactor = destAlphaFactorIn;
      this.opaque = opaqueIn;
      this.blendFunction = blendFunctionIn;
   }

   public JSONBlendingMode() {
      this(false, true, 1, 0, 1, 0, 32774);
   }

   public JSONBlendingMode(int srcFactor, int dstFactor, int blendFunctionIn) {
      this(false, false, srcFactor, dstFactor, srcFactor, dstFactor, blendFunctionIn);
   }

   public JSONBlendingMode(int srcColorFactorIn, int destColorFactorIn, int srcAlphaFactorIn, int destAlphaFactorIn, int blendFunctionIn) {
      this(true, false, srcColorFactorIn, destColorFactorIn, srcAlphaFactorIn, destAlphaFactorIn, blendFunctionIn);
   }

   public void apply() {
      if (!this.equals(lastApplied)) {
         if (lastApplied == null || this.opaque != lastApplied.isOpaque()) {
            lastApplied = this;
            if (this.opaque) {
               RenderSystem.disableBlend();
               return;
            }

            RenderSystem.enableBlend();
         }

         RenderSystem.blendEquation(this.blendFunction);
         if (this.separateBlend) {
            RenderSystem.blendFuncSeparate(this.srcColorFactor, this.destColorFactor, this.srcAlphaFactor, this.destAlphaFactor);
         } else {
            RenderSystem.blendFunc(this.srcColorFactor, this.destColorFactor);
         }

      }
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof JSONBlendingMode)) {
         return false;
      } else {
         JSONBlendingMode jsonblendingmode = (JSONBlendingMode)p_equals_1_;
         if (this.blendFunction != jsonblendingmode.blendFunction) {
            return false;
         } else if (this.destAlphaFactor != jsonblendingmode.destAlphaFactor) {
            return false;
         } else if (this.destColorFactor != jsonblendingmode.destColorFactor) {
            return false;
         } else if (this.opaque != jsonblendingmode.opaque) {
            return false;
         } else if (this.separateBlend != jsonblendingmode.separateBlend) {
            return false;
         } else if (this.srcAlphaFactor != jsonblendingmode.srcAlphaFactor) {
            return false;
         } else {
            return this.srcColorFactor == jsonblendingmode.srcColorFactor;
         }
      }
   }

   public int hashCode() {
      int i = this.srcColorFactor;
      i = 31 * i + this.srcAlphaFactor;
      i = 31 * i + this.destColorFactor;
      i = 31 * i + this.destAlphaFactor;
      i = 31 * i + this.blendFunction;
      i = 31 * i + (this.separateBlend ? 1 : 0);
      i = 31 * i + (this.opaque ? 1 : 0);
      return i;
   }

   public boolean isOpaque() {
      return this.opaque;
   }

   /**
    * Converts a blend function name to an id, returning add (32774) if not recognized.
    */
   public static int stringToBlendFunction(String funcName) {
      String s = funcName.trim().toLowerCase(Locale.ROOT);
      if ("add".equals(s)) {
         return 32774;
      } else if ("subtract".equals(s)) {
         return 32778;
      } else if ("reversesubtract".equals(s)) {
         return 32779;
      } else if ("reverse_subtract".equals(s)) {
         return 32779;
      } else if ("min".equals(s)) {
         return 32775;
      } else {
         return "max".equals(s) ? '\u8008' : '\u8006';
      }
   }

   public static int stringToBlendFactor(String factorName) {
      String s = factorName.trim().toLowerCase(Locale.ROOT);
      s = s.replaceAll("_", "");
      s = s.replaceAll("one", "1");
      s = s.replaceAll("zero", "0");
      s = s.replaceAll("minus", "-");
      if ("0".equals(s)) {
         return 0;
      } else if ("1".equals(s)) {
         return 1;
      } else if ("srccolor".equals(s)) {
         return 768;
      } else if ("1-srccolor".equals(s)) {
         return 769;
      } else if ("dstcolor".equals(s)) {
         return 774;
      } else if ("1-dstcolor".equals(s)) {
         return 775;
      } else if ("srcalpha".equals(s)) {
         return 770;
      } else if ("1-srcalpha".equals(s)) {
         return 771;
      } else if ("dstalpha".equals(s)) {
         return 772;
      } else {
         return "1-dstalpha".equals(s) ? 773 : -1;
      }
   }
}