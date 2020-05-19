package net.minecraft.client.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC10;

@OnlyIn(Dist.CLIENT)
public class ALUtils {
   private static final Logger LOGGER = LogManager.getLogger();

   private static String func_216482_a(int p_216482_0_) {
      switch(p_216482_0_) {
      case 40961:
         return "Invalid name parameter.";
      case 40962:
         return "Invalid enumerated parameter value.";
      case 40963:
         return "Invalid parameter parameter value.";
      case 40964:
         return "Invalid operation.";
      case 40965:
         return "Unable to allocate memory.";
      default:
         return "An unrecognized error occurred.";
      }
   }

   static boolean checkALError(String p_216483_0_) {
      int i = AL10.alGetError();
      if (i != 0) {
         LOGGER.error("{}: {}", p_216483_0_, func_216482_a(i));
         return true;
      } else {
         return false;
      }
   }

   private static String initErrorMessage(int p_216480_0_) {
      switch(p_216480_0_) {
      case 40961:
         return "Invalid device.";
      case 40962:
         return "Invalid context.";
      case 40963:
         return "Illegal enum.";
      case 40964:
         return "Invalid value.";
      case 40965:
         return "Unable to allocate memory.";
      default:
         return "An unrecognized error occurred.";
      }
   }

   static boolean checkALCError(long p_216481_0_, String p_216481_2_) {
      int i = ALC10.alcGetError(p_216481_0_);
      if (i != 0) {
         LOGGER.error("{}{}: {}", p_216481_2_, p_216481_0_, initErrorMessage(i));
         return true;
      } else {
         return false;
      }
   }

   static int getFormat(AudioFormat p_216479_0_) {
      Encoding encoding = p_216479_0_.getEncoding();
      int i = p_216479_0_.getChannels();
      int j = p_216479_0_.getSampleSizeInBits();
      if (encoding.equals(Encoding.PCM_UNSIGNED) || encoding.equals(Encoding.PCM_SIGNED)) {
         if (i == 1) {
            if (j == 8) {
               return 4352;
            }

            if (j == 16) {
               return 4353;
            }
         } else if (i == 2) {
            if (j == 8) {
               return 4354;
            }

            if (j == 16) {
               return 4355;
            }
         }
      }

      throw new IllegalArgumentException("Invalid audio format: " + p_216479_0_);
   }
}