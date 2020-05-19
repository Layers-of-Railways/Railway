package net.minecraft.potion;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class EffectUtils {
   @OnlyIn(Dist.CLIENT)
   public static String getPotionDurationString(EffectInstance effect, float durationFactor) {
      if (effect.getIsPotionDurationMax()) {
         return "**:**";
      } else {
         int i = MathHelper.floor((float)effect.getDuration() * durationFactor);
         return StringUtils.ticksToElapsedTime(i);
      }
   }

   public static boolean hasMiningSpeedup(LivingEntity p_205135_0_) {
      return p_205135_0_.isPotionActive(Effects.HASTE) || p_205135_0_.isPotionActive(Effects.CONDUIT_POWER);
   }

   public static int getMiningSpeedup(LivingEntity p_205134_0_) {
      int i = 0;
      int j = 0;
      if (p_205134_0_.isPotionActive(Effects.HASTE)) {
         i = p_205134_0_.getActivePotionEffect(Effects.HASTE).getAmplifier();
      }

      if (p_205134_0_.isPotionActive(Effects.CONDUIT_POWER)) {
         j = p_205134_0_.getActivePotionEffect(Effects.CONDUIT_POWER).getAmplifier();
      }

      return Math.max(i, j);
   }

   public static boolean canBreatheUnderwater(LivingEntity p_205133_0_) {
      return p_205133_0_.isPotionActive(Effects.WATER_BREATHING) || p_205133_0_.isPotionActive(Effects.CONDUIT_POWER);
   }
}